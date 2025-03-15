package gitinternals

import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream
import kotlin.math.min

fun inflateZlibFile(inputFile: File) : String {
    FileInputStream(inputFile).use { fis ->
        InflaterInputStream(fis).use { inflaterStream ->
            return inflaterStream.readAllBytes().map { Char(it.toUShort())}.joinToString("").replace("\u0000", "\n")
        }
    }
}

fun getHeader(objectPath: String): List<String> {
    return inflateZlibFile(File(objectPath)).substringBefore('\n').split(" ")
}

@OptIn(ExperimentalStdlibApi::class)
fun parseTreeObject(text: String): String {
    var text = text
    text = text.dropWhile {
        it != '\n'
    }.drop(1)

    val output = StringBuilder();

    output.append("*TREE*\n")

    var ptr = 0
    while (ptr < text.length) {
        var pos = text.indexOf(' ', startIndex = ptr)
        val permissionNumber = text.substring(ptr, pos).toInt()
        ptr = pos + 1

        pos = text.indexOf('\n', startIndex = ptr)
        val fileName = text.substring(ptr, pos)
        ptr = pos + 1

        pos = min(text.length, ptr + 20)
        val hash = text.substring(ptr, pos).toString().map { it.code.toByte().toHexString() }.joinToString("")
        ptr = pos

        output.append("$permissionNumber $hash $fileName\n")
    }
    return output.toString()
}

fun getTimestamp(timeData: List<String>, isAuthor: Boolean = false): String{
    val epochSeconds = timeData[0].toLong()
    check (timeData[1].length >= 4)

    val offset = ZoneOffset.of(timeData[1].take(timeData[1].length - 2) + ":" + timeData[1].takeLast(2))
    val instant = Instant.ofEpochSecond(epochSeconds)
    val offsetDateTime = instant.atOffset(offset)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
    val timestamp = (if (isAuthor) "original " else "commit ") + "timestamp: " + offsetDateTime.format(formatter)
    return timestamp
}

fun getFormattedRoleLine(line: String): String{
    var line = line.filter { it != '<' && it != '>'}

    val timeData = line.split(" ").takeLast(2)
    val timestamp = getTimestamp(timeData, line.startsWith("author"))
    val LineTokens = line.split(" ").toMutableList()
    LineTokens.removeLast()
    LineTokens.removeLast()
    LineTokens.add(timestamp)
    line = LineTokens.joinToString(" ").replaceFirst(" ", ": ")
    return line
}

fun parseCommitObject(text: String): String {
    var text = text
    text = text.dropWhile {
        it != '\n'
    }.drop(1)

    var lines = text.split('\n').toMutableList()

    if (lines.find { it.startsWith("parent")} != null) {
        val parentsLine =
            "parents: " + lines.filter { it.startsWith("parent") }.joinToString(" | ") { it.drop("parent ".length) }
        lines = lines.filter { !it.startsWith("parent") }.toMutableList()
        lines.add(1, parentsLine)
    }

    var authorLine = lines.find { it.startsWith("author") } ?: throw IllegalArgumentException("Unexpected for commit: Object doesn't contain author line")
    authorLine = getFormattedRoleLine(authorLine)
    var committerLine = lines.find { it.startsWith("committer") } ?: throw IllegalArgumentException("Unexpected for commit: Object doesn't contain committer line")
    committerLine = getFormattedRoleLine(committerLine)

    var treeLine = lines.find { it.startsWith("tree")} ?: throw IllegalArgumentException("Unexpected for commit: Object doesn't contain tree line")
    treeLine = treeLine.replaceFirst(" ", ": ")

    lines.replaceAll {
        when {
            it.startsWith("author") -> authorLine
            it.startsWith("committer") -> committerLine
            it.startsWith("tree") -> treeLine
            else -> it
        }
    }

    lines.add(lines.indexOfFirst { it.startsWith("committer")} + 1, "commit message:")

    return "*COMMIT*\n" + lines.filter { it.isNotEmpty()}.joinToString("\n")
}

fun parseBlobObject(text: String): String {
    var text = text
    text = text.dropWhile {
        it != '\n'
    }.drop(1)
    return "*BLOB*\n$text"
}


class Git(val dotGitPath: String) {
    fun catFile(hash: String): String {
        val objectPath = dotGitPath + "/objects/${hash.take(2)}/${hash.drop(2)}"

        val text = inflateZlibFile(File(objectPath))
        if (getHeader(objectPath)[0] == "tree") {
            return parseTreeObject(text)
        }
        if (getHeader(objectPath)[0] == "commit") {
            return parseCommitObject(text)
        }
        if (getHeader(objectPath)[0] == "blob") {
            return parseBlobObject(text)
        }

        return ""
    }
    fun listBranches(): String {
        val branchesPath = "$dotGitPath/refs/heads/"

        val branchesDirectory = File(branchesPath)
        val files = branchesDirectory.listFiles()?.toList()
            ?.map { it.toString().removePrefix(branchesPath)}
            ?.sorted()

        if (files == null) {
            return "" // TODO exception
        }

        val currentBranch = File("$dotGitPath/HEAD").readText().removePrefix("ref: refs/heads/").dropLast(1)

        return files.joinToString("\n") { if (it == currentBranch) "* $it" else "  $it" }
    }

    fun traverseBranch(commitHash: String, output: StringBuilder) {
        val commitData = catFile(commitHash)

        val parentsLine = commitData.split('\n').find { it.startsWith("parents")}

        val commitLine = "Commit: $commitHash"
        val authorLine = commitData.split('\n').find { it.startsWith("committer") }?.removePrefix("committer: ")
        val messageLine = commitData.split('\n').takeLastWhile { !it.startsWith("commit message")}.filter {it.isNotEmpty()}.joinToString("\n")
        output.append(commitLine + '\n')
        output.append(authorLine + '\n')
        output.append(messageLine + "\n")

        val isMerged = parentsLine?.contains('|')
        if (isMerged == true) {
            output.append('\n')

            val commitHash = parentsLine.takeLastWhile {it != '|'}.drop(1)
            val commitData = catFile(commitHash)

            val commitLine = "Commit: $commitHash (merged)"
            val authorLine = commitData.split('\n').find { it.startsWith("committer") }?.removePrefix("committer: ")
            val messageLine = commitData.split('\n').takeLastWhile { !it.startsWith("commit message")}.filter { it.isNotEmpty() }.joinToString("\n")
            output.append(commitLine + '\n')
            output.append(authorLine + '\n')
            output.append(messageLine + "\n")
        }

        if (parentsLine == null) {
            return
        }
        val parentHash = parentsLine.takeWhile { it != '|' }.removePrefix("parents: ").dropLastWhile() {it == ' '}

        output.append('\n')
        traverseBranch(parentHash, output)
    }

    fun log(branch: String): String {
        val branchPath = "$dotGitPath/refs/heads/$branch"

        val branchFile = File(branchPath)
        if (!branchFile.exists()) {
            return "" // TODO exception
        }

        val commitHash = branchFile.readText().dropLast(1)

        val output = StringBuilder()
        traverseBranch(commitHash, output)

        return output.toString()
    }

    fun traverseTree(treeHash: String, curPath: String, output: StringBuilder) {
        val treeData = catFile(treeHash).split("\n").drop(1).dropLast(1)
        treeData.forEach {
            val subtreeHash = it.dropWhile { it != ' ' }.dropLastWhile {it != ' '}.drop(1).dropLast(1)

            val name = it.takeLastWhile {it != ' '}

            val subtreeData = catFile(subtreeHash).split("\n")
            if (subtreeData[0] == "*BLOB*") {
                output.append(curPath + name + "\n")
            } else {
                check(subtreeData[0] == "*TREE*")

                traverseTree(subtreeHash, "$curPath$name/", output)
            }
        }

    }

    fun commitTree(commitHash: String): String {
        val treeHash = catFile(commitHash).split("\n").find {it.startsWith("tree")}?.removePrefix("tree: ")
        if (treeHash == null) {
            return "" // TODO exception
        }

        val output = StringBuilder()
        traverseTree(treeHash, "", output)

        return output.toString().removeSuffix("\n")
    }
}


fun main() {
    println("Enter .git directory location:")
    val dotGitPath = readln()

    println("Enter command:")
    val command = readln()

    val git = Git(dotGitPath)

    if (command == "list-branches") {
        println(git.listBranches())
    }

    if (command == "cat-file") {
        println("Enter git object hash:")
        val hash = readln()
        println(git.catFile(hash))
    }

    if (command == "log") {
        println("Enter branch name:")
        val branch = readln()

        println(git.log(branch))
    }

    if (command == "commit-tree") {
        println("Enter commit-hash:")
        val hash = readln()

        println(git.commitTree(hash))
    }
}

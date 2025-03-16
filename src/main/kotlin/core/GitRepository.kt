package gitinternals.core

import gitinternals.services.GitCommitTreeService
import gitinternals.services.GitLogService
import java.io.File
import java.io.FileInputStream
import java.util.zip.InflaterInputStream

class GitRepository(val gitPath: String) {
    private val gitObjects = mutableMapOf<String, GitObject>()
    private val logService = GitLogService(this)
    private val commitTreeService = GitCommitTreeService(this)

    fun catFile(hash: String): String {
        val gitObject = getObject(hash) ?: throw IllegalArgumentException("Unable to get object with provided hash")
        return gitObject.catFile()
    }

    fun listBranches(): String {
        val branchesPath = "$gitPath/refs/heads/"

        val branchesDirectory = File(branchesPath)
        val files = branchesDirectory.listFiles()?.toList()
            ?.map { it.toString().removePrefix(branchesPath) }
            ?.sorted() ?: throw IllegalArgumentException("Unable to access repository branches directory")

        val currentBranch = File("$gitPath/HEAD").readText().removePrefix("ref: refs/heads/").dropLast(1)

        return files.joinToString("\n") { if (it == currentBranch) "* $it" else "  $it" }
    }

    fun log(branch: String): String {
        return logService.log(branch)
    }

    fun commitTree(hash: String): String {
        return commitTreeService.commitTree(hash)
    }

    fun getObject(hash: String): GitObject? {
        return gitObjects[hash] ?: loadObject(hash)
    }

    private fun loadObject(hash: String): GitObject? {
        val objectPath = gitPath + "/objects/${hash.take(2)}/${hash.drop(2)}"
        val objectFile = File(objectPath)
        if (!objectFile.exists()) {
            return null
        }
        val rawData = inflateZlibFile(objectFile)

        val gitObject = GitObjectFactory.create(hash, rawData)
        gitObjects[hash] = gitObject
        return gitObject
    }

    private fun inflateZlibFile(inputFile: File): String {
        FileInputStream(inputFile).use { fis ->
            InflaterInputStream(fis).use { inflaterStream ->
                return inflaterStream.readAllBytes().map { Char(it.toUShort()) }.joinToString("")
                    .replace("\u0000", "\n")
            }
        }
    }
}
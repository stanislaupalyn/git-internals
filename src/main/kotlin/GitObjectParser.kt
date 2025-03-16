package gitinternals

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.min

object GitObjectParser {
    fun parseBlobContent(rawData: String): String {
        return rawData.dropWhile { it != '\n' }.drop(1)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun parseTreeEntries(rawData: String): List<TreeEntry> {
        var rawData = rawData
        rawData = rawData.dropWhile { it != '\n' }.drop(1)

        val entries = mutableListOf<TreeEntry>()

        var ptr = 0
        while (ptr < rawData.length) {
            var pos = rawData.indexOf(' ', startIndex = ptr)
            val permissionNumber = rawData.substring(ptr, pos)
            ptr = pos + 1

            pos = rawData.indexOf('\n', startIndex = ptr)
            val fileName = rawData.substring(ptr, pos)
            ptr = pos + 1

            pos = min(rawData.length, ptr + 20)
            val hash = rawData.substring(ptr, pos).toString().map { it.code.toByte().toHexString() }.joinToString("")
            ptr = pos

            entries.add(TreeEntry(permissionNumber, hash, fileName))
        }
        return entries
    }

    fun parseGitTimestamp(timestamp: String): ZonedDateTime {
        val parts = timestamp.split(" ")
        require(parts.size == 2) { "Invalid timestamp format: $timestamp" }

        val epochSeconds = parts[0].toLong()
        val offsetMinutes = parts[1].toInt() / 100 * 60  // Convert `+0300` to minutes

        val zoneOffset = ZoneOffset.ofTotalSeconds(offsetMinutes * 60)
        return Instant.ofEpochSecond(epochSeconds).atZone(zoneOffset)
    }

    fun parseCommitData(rawData: String): CommitData {
        var rawData = rawData
        rawData = rawData.dropWhile { it != '\n' }.drop(1)

        var lines = rawData.split('\n')
        val treeHash = lines.find { it.startsWith("tree") }?.removePrefix("tree ")
            ?: throw IllegalArgumentException("Commit doesn't contain tree hash")

        val parents = lines.filter { it.startsWith("parent") }.map { it.removePrefix("parent ") }

        val authorLineTokens = lines.find { it.startsWith("author") }?.split(" ")
            ?: throw IllegalArgumentException("Commit doesn't contain author line")
        val author = Person(
            name = authorLineTokens[1],
            email = authorLineTokens[2].drop(1).dropLast(1),
            timestamp = parseGitTimestamp(authorLineTokens[3] + " " + authorLineTokens[4])
        )

        val committerLineTokens = lines.find { it.startsWith("committer") }?.split(" ")
            ?: throw IllegalArgumentException("Commit doesn't contain committer line")
        val committer = Person(
            name = committerLineTokens[1],
            email = committerLineTokens[2].drop(1).dropLast(1),
            timestamp = parseGitTimestamp(committerLineTokens[3] + " " + committerLineTokens[4])
        )

        val commitMessage = lines.takeLastWhile { !it.startsWith("committer") }.drop(1).dropLast(1).joinToString("\n")

        return CommitData(treeHash, parents, author, committer, commitMessage)
    }
}
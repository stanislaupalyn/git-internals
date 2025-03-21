package gitinternals.core

import gitinternals.models.CommitData
import gitinternals.models.Person
import gitinternals.models.TreeEntry
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
            name = authorLineTokens.drop(1).takeWhile { !it.startsWith('<') }.joinToString(" "),
            email = authorLineTokens.find { it.startsWith('<') }?.drop(1)?.dropLast(1)
                ?: throw IllegalArgumentException("Commit doesn't contain author email"),
            timestamp = parseGitTimestamp(authorLineTokens.dropLast(1).last() + " " + authorLineTokens.last())
        )

        val committerLineTokens = lines.find { it.startsWith("committer") }?.split(" ")
            ?: throw IllegalArgumentException("Commit doesn't contain committer line")
        val committer = Person(
            name = committerLineTokens.drop(1).takeWhile { !it.startsWith('<') }.joinToString(" "),
            email = committerLineTokens.find { it.startsWith('<') }?.drop(1)?.dropLast(1)
                ?: throw IllegalArgumentException("Commit doesn't contain committer email"),
            timestamp = parseGitTimestamp(committerLineTokens.dropLast(1).last() + " " + committerLineTokens.last())
        )

        val commitMessage = lines.takeLastWhile { !it.startsWith("committer") }.drop(1).dropLast(1).joinToString("\n")

        return CommitData(treeHash, parents, author, committer, commitMessage)
    }
}
package gitinternals

object GitObjectFactory {
    private fun getHeader(content: String): GitObjectHeader {
        val type = content.takeWhile { it != ' ' }
        check(type == "tree" || type == "commit" || type == "blob") {
            "Unexpected GitObject type"
        }
        val length = content.dropWhile { it != ' ' }.drop(1).takeWhile { it != '\n' }.toInt()

        return GitObjectHeader(type, length)
    }

    fun create(hash: String, rawData: String): GitObject {
        val header = getHeader(rawData)

        return when (header.type) {
            "blob" -> {
                Blob(hash, header, GitObjectParser.parseBlobContent(rawData))
            }

            "tree" -> {
                Tree(hash, header, GitObjectParser.parseTreeEntries(rawData))
            }

            "commit" -> {
                Commit(hash, header, GitObjectParser.parseCommitData(rawData))
            }

            else -> throw IllegalArgumentException("Unknown object type: ${header.type}")
        }
    }
}
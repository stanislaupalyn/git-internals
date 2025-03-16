package gitinternals

import java.time.format.DateTimeFormatter

class Commit(override val hash: String, override val header: GitObjectHeader, val data: CommitData) : GitObject() {
    override fun catFile(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
        val output = """
            *COMMIT*
            tree: ${data.treeHash}
            parents: ${data.parents.joinToString(" | ")}
            author: ${data.author.name} ${data.author.email} original timestamp: ${formatter.format(data.author.timestamp)}
            committer: ${data.committer.name} ${data.committer.email} commit timestamp: ${formatter.format(data.committer.timestamp)}
            commit message:
            ${data.commitMessage}
        """.trimIndent()

        if (data.parents.isEmpty()) {
            return output.replace("parents: \n", "")
        }
        return output
    }
}
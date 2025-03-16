package gitinternals.services

import gitinternals.core.GitRepository
import gitinternals.models.Commit
import java.io.File
import java.time.format.DateTimeFormatter

class GitLogService(private val repository: GitRepository) {
    fun log(branch: String): String {
        val branchPath = "${repository.gitPath}/refs/heads/$branch"
        val branchFile = File(branchPath)
        if (!branchFile.exists()) {
            throw IllegalArgumentException("Unable to access specified branch")
        }

        val commitHash = branchFile.readText().dropLast(1)
        val output = StringBuilder()
        traverseBranch(commitHash, output)

        return output.toString()
    }

    private fun traverseBranch(commitHash: String, output: StringBuilder) {
        val commit = repository.getObject(commitHash) as? Commit
            ?: throw IllegalArgumentException("Provided hash doesn't correspond to commit")

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")

        if (output.isNotEmpty()) {
            output.append('\n')
        }
        output.append("Commit: $commitHash\n")
        output.append(
            "${commit.data.committer.name} ${commit.data.committer.email} commit timestamp: ${
                formatter.format(
                    commit.data.committer.timestamp
                )
            }\n"
        )
        output.append("${commit.data.commitMessage}\n")

        if (commit.data.parents.size > 1) {
            val commitHash = commit.data.parents[1]
            val commit = repository.getObject(commitHash) as? Commit
                ?: throw IllegalArgumentException("Provided hash doesn't correspond to commit")

            if (output.isNotEmpty()) {
                output.append('\n')
            }
            output.append("Commit: $commitHash (merged)\n")
            output.append(
                "${commit.data.committer.name} ${commit.data.committer.email} commit timestamp: ${
                    formatter.format(
                        commit.data.committer.timestamp
                    )
                }\n"
            )
            output.append("${commit.data.commitMessage}\n")
        }

        if (commit.data.parents.isNotEmpty()) {
            traverseBranch(commit.data.parents[0], output)
        }
    }
}
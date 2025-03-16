package gitinternals.models

data class CommitData(
    val treeHash: String,
    val parents: List<String>,
    val author: Person,
    val committer: Person,
    val commitMessage: String
)
package gitinternals

data class TreeEntry(
    val mode: String,
    val hash: String,
    val name: String
)
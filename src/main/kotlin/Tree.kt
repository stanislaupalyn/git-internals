package gitinternals

class Tree(override val hash: String, override val header: GitObjectHeader, val entries: List<TreeEntry>) :
    GitObject() {
    override fun catFile(): String {
        var entriesLines = entries.joinToString("\n") { "${it.mode} ${it.hash} ${it.name}" }
        return "*TREE*\n$entriesLines"
    }
}
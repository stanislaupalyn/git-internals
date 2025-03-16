package gitinternals.core

abstract class GitObject {
    abstract val hash: String
    abstract val header: GitObjectHeader

    abstract fun catFile(): String
}
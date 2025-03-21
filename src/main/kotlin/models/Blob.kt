package gitinternals.models

import gitinternals.core.GitObject
import gitinternals.core.GitObjectHeader

class Blob(override val hash: String, override val header: GitObjectHeader, val content: String) : GitObject() {
    override fun catFile(): String {
        return "*BLOB*\n$content"
    }
}
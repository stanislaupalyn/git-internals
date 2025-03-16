package gitinternals.commands

import gitinternals.core.GitRepository

class CatFileCommand(private val repository: GitRepository, private val hash: String) : Command {
    override fun execute(): String {
        return repository.catFile(hash)
    }
}
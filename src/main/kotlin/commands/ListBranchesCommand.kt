package gitinternals.commands

import gitinternals.core.GitRepository

class ListBranchesCommand(private val repository: GitRepository) : Command {
    override fun execute(): String {
        return repository.listBranches()
    }
}
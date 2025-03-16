package gitinternals.commands

import gitinternals.core.GitRepository

object CommandFactory {
    fun createCommand(args: List<String>, gitRepository: GitRepository): Command {
        return when (args.first()) {
            "list-branches" -> ListBranchesCommand(gitRepository)
            "cat-file" -> CatFileCommand(gitRepository, args[1])
            "commit-tree" -> CommitTreeCommand(gitRepository, args[1])
            "log" -> LogCommand(gitRepository, args[1])
            else -> throw IllegalArgumentException("Unknown command: ${args.first()}")
        }
    }
}
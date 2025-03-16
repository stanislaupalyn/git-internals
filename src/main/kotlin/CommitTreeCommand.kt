package gitinternals

class CommitTreeCommand(private val repository: GitRepository, private val hash: String) : Command {
    override fun execute(): String {
        return repository.commitTree(hash)
    }
}
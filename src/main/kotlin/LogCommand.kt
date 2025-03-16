package gitinternals

class LogCommand(private val repository: GitRepository, private val branch: String) : Command {
    override fun execute(): String {
        return repository.log(branch)
    }
}

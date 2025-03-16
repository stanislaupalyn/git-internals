package gitinternals

class CatFileCommand(private val repository: GitRepository, private val hash: String) : Command {
    override fun execute(): String {
        return repository.catFile(hash)
    }
}
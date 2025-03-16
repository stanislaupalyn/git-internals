package gitinternals.commands

interface Command {
    fun execute(): String
}
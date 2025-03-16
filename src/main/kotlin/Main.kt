package gitinternals

import gitinternals.commands.CommandFactory
import gitinternals.core.GitRepository

fun main() {
    println("Enter .git directory location:")
    val gitPath = readln()
    val commandArgs = parseUserCommand() ?: return

    try {
        val repository = GitRepository(gitPath)
        val command = CommandFactory.createCommand(commandArgs, repository)
        println(command.execute())
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

fun parseUserCommand(): List<String>? {
    println("Enter command:")
    val command = readln()

    val additionalArg = when (command) {
        "cat-file" -> promptForArgument("Enter git object hash:")
        "log" -> promptForArgument("Enter branch name:")
        "commit-tree" -> promptForArgument("Enter commit-hash:")
        "list-branches" -> null
        else -> {
            println("Unknown command")
            return null
        }
    }

    return if (additionalArg != null) listOf(command, additionalArg) else listOf(command)
}

fun promptForArgument(prompt: String): String {
    println(prompt)
    return readln()
}
package gitinternals

import gitinternals.commands.CommandFactory
import gitinternals.core.GitRepository

fun main() {
    println("Enter .git directory location:")
    val gitPath = readln()
    println("Enter command:")
    val command = readln()

    val repository = GitRepository(gitPath)

    var args = listOf<String>()
    when (command) {
        "cat-file" -> {
            println("Enter git object hash:")
            val hash = readln()

            args = listOf("cat-file", hash)
        }

        "list-branches" -> {
            args = listOf("list-branches")
        }

        "log" -> {
            println("Enter branch name:")
            val branch = readln()

            args = listOf("log", branch)
        }

        "commit-tree" -> {
            println("Enter commit-hash:")
            val hash = readln()

            args = listOf("commit-tree", hash)
        }
    }

    try {
        val command = CommandFactory.createCommand(args, repository)
        println(command.execute())
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

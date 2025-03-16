package gitinternals.services

import gitinternals.core.GitRepository
import gitinternals.models.Blob
import gitinternals.models.Commit
import gitinternals.models.Tree

class GitCommitTreeService(private val repository: GitRepository) {
    fun commitTree(commitHash: String): String {
        val commit = repository.getObject(commitHash) as? Commit
            ?: throw IllegalArgumentException("Provided hash doesn't correspond to commit")

        val treeHash = commit.data.treeHash

        val output = StringBuilder()
        traverseTree(treeHash, "", output)

        return output.toString().removeSuffix("\n")
    }

    private fun traverseTree(treeHash: String, curPath: String, output: StringBuilder) {
        val tree = repository.getObject(treeHash) as? Tree
            ?: throw IllegalArgumentException("Provided hash doesn't correspond to tree")

        tree.entries.forEach {
            val entryObject =
                repository.getObject(it.hash) ?: return

            if (entryObject.header.type == "tree") {
                traverseTree(entryObject.hash, "$curPath${it.name}/", output)
            } else {
                if (entryObject !is Blob) {
                    throw IllegalArgumentException("Unexpected type of tree entry")
                }

                output.append(curPath + it.name + "\n")
            }
        }
    }
}
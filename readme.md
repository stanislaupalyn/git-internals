# Git Internals

A Kotlin application that allows users to inspect the .git folder of a Git repository. It provides commands to analyze Git objects such as commits, trees, and blobs.

Inspired by [Hyperskill Kotlin Project](https://hyperskill.org/projects/110)

## Features

- List branches (`list-branches`): Displays all branches in the repository.

- Inspect objects (`cat-file <hash>`): Outputs the content of a Git object (blob, tree, or commit).

- View commit history (`log <branch>`): Shows commit history of specified branch.

- Display commit tree (`commit-tree <commit-hash>`): Prints the tree structure of a given commit.


## Installation

Clone the repository

```
git clone git@github.com:stanislaupalyn/git-internals.git
cd git-internals
```

Build the project

```
gradle build
```

## Usage

```
java -jar build/libs/git-internals.jar
```

The program interacts through standard input and prints the results to standard output.
Once started, the program waits for user input, providing a message of what is expected. 

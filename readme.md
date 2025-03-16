# Git Internals

A Kotlin application that allows users to inspect the .git folder of a Git repository. It provides commands to analyze Git objects such as commits, trees, and blobs.

Inspired by [Hyperskill Kotlin Project](https://hyperskill.org/projects/110)

## Features

- List branches (`list-branches`): Displays all branches in the repository.

- Inspect objects (`cat-file <hash>`): Outputs the content of a Git object (blob, tree, or commit).

- View commit history (`log <branch>`): Shows commit history of specified branch.

- Display commit tree (`commit-tree <commit-hash>`): Prints the tree structure of a given commit.


## Examples

```
Enter .git directory location:
> .git
Enter command:
> commit-tree
Enter commit-hash:
> fd362f3f305819d17b4359444aa83e17e7d6924a
main.kt
readme.txt
some-folder/qq.txt
```

```
Enter .git directory location:
> .git
Enter command:
> cat-file
Enter git object hash:
> 490f96725348e92770d3c6bab9ec532564b7ebe0
*BLOB*
fun main() {
    while(true) {
        println("Hello World!")
    }
} 

```

```
Enter .git directory location:
> .git
Enter command:
> list-branches
  feature1
  feature2
* master
```

```
Enter .git directory location:
> .git
Enter command:
> log
Enter branch name:
> main
Commit: 42571ef4697e4ee631422afaefefa78825181d1c
Stanislau Palyn stanislavpolyn@yandex.ru commit timestamp: 2025-03-16 16:37:19 +01:00
Improve readability

Commit: 8cca1c70854c2c9d4893b885533fa399104c7656
Stanislau Palyn stanislavpolyn@yandex.ru commit timestamp: 2025-03-16 16:05:10 +01:00
Fix readme git log description
...
```

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
Once started, the program waits for user input, providing a message, describing what input is expected. 

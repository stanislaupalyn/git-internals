import gitinternals.main
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class GitInternalsTest {
    companion object {
        const val GIT_ONE_PATH = "src/test/resources/gitone"
        const val GIT_TWO_PATH = "src/test/resources/gittwo"
    }

    @Test
    fun `test commit tree 1`() {
        val inputText = """
            $GIT_ONE_PATH
            commit-tree
            fd362f3f305819d17b4359444aa83e17e7d6924a
        """.trimIndent()

        val expectedOutput = """
           Enter .git directory location:
           Enter command:
           Enter commit-hash:
           main.kt
           readme.txt
           some-folder/qq.txt
           
        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        assertEquals(expectedOutput, outputStream.toString())
    }

    @Test
    fun `test commit tree 2`() {
        val inputText = """
            $GIT_ONE_PATH
            commit-tree
            12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
        """.trimIndent()

        val expectedOutput = """
           Enter .git directory location:
           Enter command:
           Enter commit-hash:
           main.kt

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        assertEquals(expectedOutput, outputStream.toString())
    }

    @Test
    fun `test cat-file 1`() {
        val inputText = """
            $GIT_ONE_PATH
            cat-file
            0eee6a98471a350b2c2316313114185ecaf82f0e
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter git object hash:
            *COMMIT*
            tree: 79401ddb0e2c0fe0472c813754dd4a8873b66a84
            parents: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
            author: Smith mr.smith@matrix original timestamp: 2020-03-29 17:18:20 +03:00
            committer: Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
            commit message:
            get docs from feature1

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedOutput, outputStream.toString())
        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test cat-file 2`() {
        val inputText = """
            $GIT_ONE_PATH
            cat-file
            490f96725348e92770d3c6bab9ec532564b7ebe0
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter git object hash:
            *BLOB*
            fun main() {
                while(true) {
                    println("Hello Hyperskill student!")
                }
            } 


        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        assertEquals(expectedOutput.split('\n').size, outputStream.toString().split('\n').size)
        val length = expectedOutput.split('\n').size
        val expectedLines = expectedOutput.split('\n')
        val outputLines = outputStream.toString().split('\n')
        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test cat-file 3`() {
        val inputText = """
            $GIT_ONE_PATH
            cat-file
            fb043556c251cb450a0d55e4ceb1ff35e12029c3
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter git object hash:
            *TREE*
            100644 2b26c15c04375d90203783fb4c2a45ff04b571a6 main.kt
            100644 4a8abe7b618ddf9c55adbea359ce891775794a61 readme.txt

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test list-branches 1`() {
        val inputText = """
            $GIT_ONE_PATH
            list-branches
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
              feature1
              feature2
            * master

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test log 1`() {
        val inputText = """
            $GIT_ONE_PATH
            log
            master
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter branch name:
            Commit: fd362f3f305819d17b4359444aa83e17e7d6924a
            Neo neo@matrix commit timestamp: 2020-04-04 11:42:08 +03:00
            add subfolder
            
            Commit: 39a0337532d7720acc90497043e2ade92c386939
            Neo neo@matrix commit timestamp: 2020-04-04 09:59:23 +03:00
            this commit message will have multiple lines
            we need multiple lines commit message for test purposes
            3
            4
            5
            
            Commit: 6d537a47eddc11f866bcc2013703bf31bfcf9ed8
            Cypher cypher@matrix commit timestamp: 2020-03-29 17:29:08 +03:00
            Merge branch 'feature2'
            
            Commit: 97e638cc1c7135580c3ff93162e727148e1bad05 (merged)
            Cypher cypher@matrix commit timestamp: 2020-03-29 17:27:35 +03:00
            break our software
            
            Commit: 31d679c1c2b8fc787ae014c501d4fa6545faa138
            Neo neo@matrix commit timestamp: 2020-03-29 17:21:48 +03:00
            Merge branch 'feature1'
            
            Commit: 300b1c67b5539bfdcb30f2863d6ac3b3377ad00b (merged)
            Morpheus morpheus@matrix commit timestamp: 2020-03-29 17:19:49 +03:00
            continue work on documentation
            
            Commit: 4107cf41cf55c4fd38e9da8f3d08d1eaefc3254a
            Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:13:44 +03:00
            continue work
            
            Commit: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
            Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:12:52 +03:00
            start kotlin project
            
            Commit: 73324685d9dbd1fdda87f3c5c6f77d79c1b769c2
            Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:10:52 +03:00
            initial commit
            
        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test log 2`() {
        val inputText = """
            $GIT_ONE_PATH
            log
            feature2
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter branch name:
            Commit: 97e638cc1c7135580c3ff93162e727148e1bad05
            Cypher cypher@matrix commit timestamp: 2020-03-29 17:27:35 +03:00
            break our software

            Commit: 0eee6a98471a350b2c2316313114185ecaf82f0e
            Cypher cypher@matrix commit timestamp: 2020-03-29 17:25:52 +03:00
            get docs from feature1

            Commit: 12a4717e84b5e414f93cc91ca50a6d5a6c3563a0
            Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:12:52 +03:00
            start kotlin project

            Commit: 73324685d9dbd1fdda87f3c5c6f77d79c1b769c2
            Neo mr.anderson@matrix commit timestamp: 2020-03-29 17:10:52 +03:00
            initial commit

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test cat-file 4`() {
        val inputText = """
            $GIT_TWO_PATH
            cat-file
            31cddcbd00e715688cd127ad20c2846f9ed98223
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter git object hash:
            *COMMIT*
            tree: aaa96ced2d9a1c8e72c56b253a0e2fe78393feb7
            author: Kalinka Kali.k4@email.com original timestamp: 2021-12-11 22:31:36 -03:00
            committer: Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
            commit message:
            simple hello

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test cat-file 5`() {
        val inputText = """
            $GIT_TWO_PATH
            cat-file
            dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter git object hash:
            *COMMIT*
            tree: d128f76a96c56ac4373717d3fbba4fa5875ca68f
            parents: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f | d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9
            author: Kalinka Kali.k4@email.com original timestamp: 2021-12-11 22:49:02 -03:00
            committer: Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
            commit message:
            awsome hello

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test cat-file 6`() {
        val inputText = """
            $GIT_TWO_PATH
            cat-file
            aaa96ced2d9a1c8e72c56b253a0e2fe78393feb7
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter git object hash:
            *TREE*
            100644 ce013625030ba8dba906f756967f9e9ca394464a hello.txt

        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test list-branches 2`() {
        val inputText = """
            $GIT_TWO_PATH
            list-branches
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
              helloWithFeature1
              helloWithFeature2
            * main
            
        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test log 3`() {
        val inputText = """
            $GIT_TWO_PATH
            log
            main
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter branch name:
            Commit: dcec4e51e2ce4a46a6206d0d4ab33fa99d8b1ab5
            Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:49:02 -03:00
            awsome hello
            
            Commit: d2c5bedbb2c46945fd84f2ad209a7d4ee047f7f9 (merged)
            Ivan Petrovich@moon.org commit timestamp: 2021-12-11 22:43:54 -03:00
            hello of the champions
            
            Commit: 5ad3239e54ba7c533d9f215a13ac82d14197cd8f
            Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:46:28 -03:00
            maybe hello
            
            Commit: 31cddcbd00e715688cd127ad20c2846f9ed98223
            Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
            simple hello
            
        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }

    @Test
    fun `test log 4`() {
        val inputText = """
            $GIT_TWO_PATH
            log
            helloWithFeature1
        """.trimIndent()

        val expectedOutput = """
            Enter .git directory location:
            Enter command:
            Enter branch name:
            Commit: c5e4a1239e11e51e95b051e2f25c3325b5004676
            Yulia yulia@coldmail.com commit timestamp: 2021-12-11 22:36:46 -03:00
            internacional hello
            
            Commit: 31cddcbd00e715688cd127ad20c2846f9ed98223
            Kalinka Kali.k4@email.com commit timestamp: 2021-12-11 22:31:36 -03:00
            simple hello
            
        """.trimIndent()

        val inputStream = ByteArrayInputStream(inputText.toByteArray())
        System.setIn(inputStream)

        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        System.setOut(printStream)

        main()

        val expectedLines = expectedOutput.split('\n').filter {it.isNotEmpty()}
        val outputLines = outputStream.toString().split('\n').filter { it.isNotEmpty() }

        assertEquals(expectedLines.size, outputLines.size)
        val length = expectedLines.size

        for (i in 0..<length) {
            assertEquals(expectedLines[i], outputLines[i])
        }
    }
}
package gitinternals.models

import java.time.ZonedDateTime

data class Person(
    val name: String,
    val email: String,
    val timestamp: ZonedDateTime
)
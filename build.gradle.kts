plugins {
    kotlin("jvm") version "2.1.10"
    application
}

application {
    mainClass.set("gitinternals.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "gitinternals.MainKt"
    }
    from(*configurations.runtimeClasspath.get().filter { it.exists() }
        .map { if (it.isDirectory) it else zipTree(it) }
        .toTypedArray())
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
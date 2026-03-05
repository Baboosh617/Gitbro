
// build.gradle.kts  –  Gitbro build file

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application                                          
}

group   = "com.gitbro"
version = "1.0.0"


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20240303")
    implementation(kotlin("stdlib"))
}


application {
    mainClass.set("com.gitbro.MainKt")
}


tasks.shadowJar {
    archiveBaseName.set("gitbro")
    archiveVersion.set("1.0.0")
    archiveClassifier.set("") 

    manifest {
        attributes["Main-Class"] = "com.gitbro.MainKt"
    }
}


tasks.build {
    dependsOn(tasks.shadowJar)
}


kotlin {
    jvmToolchain(17)
}

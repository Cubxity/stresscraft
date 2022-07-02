plugins {
    kotlin("jvm") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.cubxity.tools"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")
    implementation("com.github.steveice10:mcprotocollib:1.19-1")
    implementation("org.fusesource.jansi:jansi:2.4.0")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "dev.cubxity.tools.stresscraft.cli.StressCraftCLIKt")
        }
    }
    shadowJar {
        archiveClassifier.set("")
    }
}

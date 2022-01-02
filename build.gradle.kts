plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.cubxity.tools"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")
    implementation("com.github.Steveice10:MCProtocolLib:1.18-2")
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

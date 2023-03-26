plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.cubxity.tools"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.5")
    implementation("com.github.steveice10:mcprotocollib:1.19.4-SNAPSHOT")
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

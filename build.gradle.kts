plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

// Paper release the plugin is compiled, tested and published against.
val paperVersion = "26.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperVersion}.build.+")
    implementation("org.bstats:bstats-bukkit:3.2.1")

    testImplementation("io.papermc.paper:paper-api:${paperVersion}.build.+")
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    // The shadow jar (with relocated bStats) is the only artifact.
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier = ""
        relocate("org.bstats", "dev.chernykh.unseenFrames.libs.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    runServer {
        // Minecraft version for the dev server. The plugin's shadow jar is picked up automatically.
        minecraftVersion(paperVersion)
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

// ./gradlew publishPluginPublicationToHangar - see README, "Releasing".
hangarPublish {
    publications.register("plugin") {
        id = "UnseenFrames"
        version = project.version as String
        channel = "Release"
        // The token is never stored in the repository: CI passes HANGAR_API_TOKEN,
        // locally it is read from the git-ignored pat_hangar file.
        apiKey = providers.environmentVariable("HANGAR_API_TOKEN")
            .orElse(providers.fileContents(layout.projectDirectory.file("pat_hangar")).asText.map { it.trim() })
        // Body of the topmost "## [x.y.z]" section of CHANGELOG.md.
        changelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText
            .map { it.substringAfter("\n## ").substringBefore("\n## ").substringAfter("\n").trim() }
        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = listOf(paperVersion)
            }
        }
        // ./gradlew syncAllPagesToHangar updates the project page from hangar/page.md.
        pages {
            resourcePage(providers.fileContents(layout.projectDirectory.file("hangar/page.md")).asText)
        }
    }
}

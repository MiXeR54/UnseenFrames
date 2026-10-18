plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
    id("com.modrinth.minotaur") version "2.10.0"
}

// Paper release the plugin is compiled, tested and run against.
val paperVersion = "26.3"

// Paper releases the plugin is published for. It only uses API that is stable across all of them,
// so api-version in plugin.yml stays at the oldest one.
val supportedPaperVersions = listOf("26.2", "26.3")

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

// Body of the topmost "## [x.y.z]" section of CHANGELOG.md, the release notes on Hangar and Modrinth.
val releaseChangelog = providers.fileContents(layout.projectDirectory.file("CHANGELOG.md")).asText
    .map { it.substringAfter("\n## ").substringBefore("\n## ").substringAfter("\n").trim() }

// The project page on both sites.
val projectPage = providers.fileContents(layout.projectDirectory.file("hangar/page.md")).asText

// Publishing tokens are never stored in the repository: CI passes them as environment variables,
// locally they are read from the git-ignored pat_* files in the project root.
fun publishToken(env: String, file: String): Provider<String> = providers.environmentVariable(env)
    .orElse(providers.fileContents(layout.projectDirectory.file(file)).asText.map { it.trim() })

// ./gradlew publishPluginPublicationToHangar - see RELEASING.md.
hangarPublish {
    publications.register("plugin") {
        id = "UnseenFrames"
        version = project.version as String
        channel = "Release"
        apiKey = publishToken("HANGAR_API_TOKEN", "pat_hangar")
        changelog = releaseChangelog
        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = supportedPaperVersions
            }
        }
        // ./gradlew syncAllPagesToHangar updates the project page from hangar/page.md.
        pages {
            resourcePage(projectPage)
        }
    }
}

// ./gradlew modrinth uploads the jar as a new version, ./gradlew modrinthSyncBody updates the project
// page from hangar/page.md - see RELEASING.md.
modrinth {
    token = publishToken("MODRINTH_TOKEN", "pat_modrinth")
    projectId = "unseenframes"
    versionNumber = project.version as String
    versionName = "UnseenFrames ${project.version}"
    versionType = "release"
    uploadFile.set(tasks.shadowJar)
    gameVersions.addAll(supportedPaperVersions)
    loaders.addAll("paper", "folia")
    changelog = releaseChangelog
    syncBodyFrom = projectPage
}

// Minotaur reads the project while its tasks run, which the configuration cache does not allow.
listOf("modrinth", "modrinthSyncBody").forEach { name ->
    tasks.named(name) { notCompatibleWithConfigurationCache("Minotaur reads the project at execution time") }
}

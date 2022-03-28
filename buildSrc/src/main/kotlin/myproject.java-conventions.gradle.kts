// Define Java conventions for this organization.
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import java.net.URI

//import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    idea

    // NOTE: external plugin version is specified in implementation dependency artifact of the project's build file
}

group = "com.fablesfantasyrp.core.${rootProject.name.toLowerCase()}"

java   {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


/*tasks {
    withType<ProcessResources> {
        filter(mapOf(Pair("tokens", mapOf(Pair("version", version)))), ReplaceTokens::class.java)
    }
    withType<ShadowJar> {
        this.classifier = null
        this.configurations = listOf(project.configurations.shadow.get())
    }
}*/

// Projects should use Maven Central for external dependencies
// This could be the organization's private repository
repositories {
    maven { url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }

    mavenCentral()
    mavenLocal()
}

//idea {
//    module {
//        isDownloadJavadoc = true
//        isDownloadSources = true
//    }
//}

// Enable deprecation messages when compiling Java code
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT") { isChanging = true }
}

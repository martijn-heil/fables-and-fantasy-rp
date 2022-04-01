// Define Java conventions for this organization.
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import java.nio.channels.Channels


plugins {
	java
	kotlin("jvm")
	id("com.github.johnrengelman.shadow")
	idea

	// NOTE: external plugin version is specified in implementation dependency artifact of the project's build file
}

group = "com.fablesfantasyrp.plugin"
val authors = "Ninjoh, Darwin"
val bukkitApiVersion = "1.18"

java   {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<ProcessResources> {
        filter(mapOf(Pair("tokens", mapOf(
				Pair("version", version),
				Pair("authors", authors),
				Pair("bukkit_api_version", bukkitApiVersion)
		))), ReplaceTokens::class.java)
	}

    withType<ShadowJar> {
        this.configurations = listOf(project.configurations.shadow.get())
    }
}

// Projects should use Maven Central for external dependencies
// This could be the organization's private repository
repositories {
	maven { url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
	maven { url = URI("https://jitpack.io") }

	mavenCentral()
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

// Enable deprecation messages when compiling Java code
tasks.withType<JavaCompile>().configureEach {
	options.compilerArgs.add("-Xlint:deprecation")
}

fun downloadFile(url: URL, outputFileName: String) {
	url.openStream().use {
		Channels.newChannel(it).use { rbc ->
			FileOutputStream(outputFileName).use { fos ->
				fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
			}
		}
	}
}

fun urlFile (url: URL, name: String): ConfigurableFileCollection  {
	val path = "$buildDir/download/${name}.jar"
	val f = File(path)
	f.parentFile.mkdirs()
	if(!f.exists()) downloadFile(url, path)
	return files(f.absolutePath)
}

dependencies {
	implementation("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT") { isChanging = true }
	implementation(urlFile(URL("https://ci.citizensnpcs.co/job/Denizen/lastSuccessfulBuild/artifact/target/Denizen-1.2.4-b1762-REL.jar"), "Denizen"))
	implementation(urlFile(URL("https://ci.ender.zone/job/EssentialsX/lastSuccessfulBuild/artifact/jars/EssentialsX-2.20.0-dev+4-4bd1b3c.jar"), "EssentialsX"))
}

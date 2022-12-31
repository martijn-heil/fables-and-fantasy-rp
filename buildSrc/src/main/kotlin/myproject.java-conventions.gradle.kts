// Define Java conventions for this organization.
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import java.nio.channels.Channels


plugins {
	java
	kotlin("jvm")
	id("com.github.johnrengelman.shadow")
	idea
	id("io.papermc.paperweight.userdev")
	id("com.vanniktech.dependency.graph.generator")

	// NOTE: external plugin version is specified in implementation dependency artifact of the project's build file
}

group = "com.fablesfantasyrp.plugin"
version = "1.0-SNAPSHOT"
val authors = "Ninjoh, darwj, Appenzell"
val bukkitApiVersion = "1.18"

base.archivesName.set("Fables${name}")

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

	withType<KotlinCompile>().configureEach {
		kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.time.ExperimentalTime"
		kotlinOptions {
			jvmTarget = "17"
		}
	}

    withType<ShadowJar> {
        this.configurations = listOf(project.configurations.shadow.get())
		this.archiveVersion.set("")
		this.archiveAppendix.set("")
    }

	assemble {
		dependsOn(reobfJar)
	}
}

// Projects should use Maven Central for external dependencies
// This could be the organization's private repository
repositories {
	maven { url = URI("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
	maven { url = URI("https://repo.extendedclip.com/content/repositories/placeholderapi") }
	maven { url = URI("https://ci.citizensnpcs.co/job/Denizen/1765/maven-repository/repository/") }
	maven { url = URI("https://repo.kryptonmc.org/releases") }
	maven { url = URI("https://repo.dmulloy2.net/repository/public/") }
	maven { url = URI("https://repo.essentialsx.net/releases/") }
	maven { url = URI("https://repo.codemc.io/repository/maven-public/") }
	maven { url = URI("https://maven.enginehub.org/repo/")}
	maven { url = URI("https://oss.sonatype.org/content/repositories/snapshots/") }
	mavenLocal()
	maven { url = URI("https://jitpack.io") }

	mavenCentral()
	maven { url = URI("https://repo.minebench.de/") }
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
val downloadPath = "${buildDir.parentFile.parent}/build/download/"
fun urlFile (url: URL, name: String): ConfigurableFileCollection  {
	val path = "$downloadPath/${name}.jar"
	val f = File(path)
	f.parentFile.mkdirs()
	if(!f.exists()) downloadFile(url, path)
	return files(f.absolutePath)
}

dependencies {
	paperDevBundle("1.19.2-R0.1-SNAPSHOT")
	implementation("net.essentialsx:EssentialsX:2.19.0") {
		exclude(group = "org.bukkit")
		exclude(group = "org.spigotmc")
		exclude(group = "io.papermc")
	}
	implementation("net.essentialsx:EssentialsXSpawn:2.19.0") {
		exclude(group = "org.bukkit")
		exclude(group = "org.spigotmc")
		exclude(group = "io.papermc")
	}
	implementation("com.github.MilkBowl:VaultAPI:1.7") {
		exclude(group = "org.bukkit")
		exclude(group = "org.spigotmc")
		exclude(group = "io.papermc")
	}
	implementation("me.clip:placeholderapi:2.10.0")
	implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.5.0")
	implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.5.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
	implementation("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
	implementation("com.google.guava:guava:31.1-jre")
	implementation(urlFile(URL("https://github.com/Gecolay/GSit/releases/download/1.2.4/GSit-1.2.4.jar"), "GSit.jar"))
	//implementation("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT") { isChanging = true }
	implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT") { isChanging = true }
	implementation(fileTree("${project.projectDir.parent}/lib") { include("*.jar") })
}

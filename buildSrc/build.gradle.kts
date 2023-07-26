import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.0")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.3")
    implementation( "com.vanniktech:gradle-dependency-graph-generator-plugin:0.7.0")
}

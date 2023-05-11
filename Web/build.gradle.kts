import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("myproject.java-conventions")
}

repositories {
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val ktor_version = "2.2.2"

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Database"))
    shadow("io.ktor:ktor-client-core:$ktor_version")
    shadow("io.ktor:ktor-client-cio:$ktor_version")
    shadow("io.ktor:ktor-client-content-negotiation:$ktor_version")
    shadow("io.ktor:ktor-server-core-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-netty-jvm:$ktor_version")
    shadow("io.ktor:ktor-server-content-negotiation:$ktor_version")
    shadow("io.ktor:ktor-server-auth:$ktor_version")
    shadow("io.ktor:ktor-server-sessions:$ktor_version")
    shadow("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    shadow("io.ktor:ktor-server-resources:$ktor_version")
    shadow("io.ktor:ktor-server-cors:$ktor_version")
    shadow("io.ktor:ktor-server-request-validation:$ktor_version")
    shadow("io.ktor:ktor-server-status-pages:$ktor_version")

    api("io.ktor:ktor-client-core:$ktor_version")
    api("io.ktor:ktor-client-cio:$ktor_version")
    api("io.ktor:ktor-client-content-negotiation:$ktor_version")
    api("io.ktor:ktor-server-core-jvm:$ktor_version")
    api("io.ktor:ktor-server-netty-jvm:$ktor_version")
    api("io.ktor:ktor-server-content-negotiation:$ktor_version")
    api("io.ktor:ktor-server-auth:$ktor_version")
    api("io.ktor:ktor-server-sessions:$ktor_version")
    api("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    api("io.ktor:ktor-server-resources:$ktor_version")
    api("io.ktor:ktor-server-cors:$ktor_version")
    api("io.ktor:ktor-server-request-validation:$ktor_version")
    api("io.ktor:ktor-server-status-pages:$ktor_version")
}

tasks {
    withType<ShadowJar> {
        exclude("kotlin/")
        //exclude("kotlinx/")
    }
}

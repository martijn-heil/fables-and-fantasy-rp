plugins {
    id("myproject.java-conventions")
}

val ktor_version = "2.2.2"

dependencies {
    // internal module dependencies
    shadow(kotlin("stdlib"))
    shadow("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    api("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.10.0")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.10.0")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
    shadow("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
    shadow("com.google.guava:guava:31.1-jre")
    shadow("io.insert-koin:koin-core:3.3.2")
    shadow("com.github.Keelar:ExprK:master-SNAPSHOT")
    shadow("dev.kord:kord-core:0.9.0")

    // Web
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
    shadow("io.ktor:ktor-server-forwarded-header:$ktor_version")
}

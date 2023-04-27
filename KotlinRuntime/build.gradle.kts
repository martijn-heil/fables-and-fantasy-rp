plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    shadow(kotlin("stdlib"))
    shadow("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    api("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.10.0")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.10.0")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4")
    shadow("org.ocpsoft.prettytime:prettytime:5.0.6.Final")
    shadow("com.google.guava:guava:31.1-jre")
    shadow("io.insert-koin:koin-core:3.3.2")
    shadow("com.github.Keelar:ExprK:master-SNAPSHOT")
}

plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    shadow(kotlin("stdlib"))
    shadow("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    api("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.5.0")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.5.0")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
}

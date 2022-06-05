plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    shadow(kotlin("stdlib"))
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:1.6.0")
    shadow("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:1.6.0")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:1.6.0")
    api("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:1.6.0")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    shadow("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
}

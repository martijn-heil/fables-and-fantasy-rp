plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    api("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
}

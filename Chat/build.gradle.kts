plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Database"))
    implementation(project(":PlayerData"))
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Characters"))
    implementation(project(":Gui"))
    implementation(project(":DenizenInterop"))
    implementation(project(":Text"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
    }
    implementation("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
}

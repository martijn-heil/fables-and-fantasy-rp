plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Database"))
    implementation(project(":PlayerData"))
    implementation(project(":Utils"))
    implementation(project(":Characters"))
    implementation(project(":Gui"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
    }
}

plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Database"))
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Characters"))
    implementation(project(":Knockout"))
    implementation(project(":Gui"))
    implementation(project(":DenizenInterop"))
    implementation(project(":Text"))
    implementation(project(":Form"))
    implementation("com.gitlab.martijn-heil:NinCommands:1.0-SNAPSHOT") { isChanging = true }
    implementation("me.neznamy:tab-api:3.0.2")
    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
    }
    implementation("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT") { isChanging = true }
}

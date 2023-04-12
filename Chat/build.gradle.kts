plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Database"))
    api(project(":Database"))
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Characters"))
    implementation(project(":Knockout"))
    implementation(project(":Gui"))
    implementation(project(":DenizenInterop"))
    implementation(project(":Text"))
    implementation(project(":Form"))
    implementation(project(":Party"))
    implementation("me.neznamy:tab-api:3.0.2")
    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
    }
}

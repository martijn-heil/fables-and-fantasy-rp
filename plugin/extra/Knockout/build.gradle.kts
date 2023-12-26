plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:extra:MoreLogging"))
    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
    }
}

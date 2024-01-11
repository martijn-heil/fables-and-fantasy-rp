plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Database"))
    api(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Form"))
	implementation(project(":plugin:core:Domain"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:extra:Knockout"))
    implementation(project(":plugin:extra:Party"))
    implementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit")
    }
}

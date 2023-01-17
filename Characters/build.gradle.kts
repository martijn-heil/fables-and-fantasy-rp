plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":DenizenInterop"))
    implementation(project(":Database"))
    implementation(project(":Gui"))
    implementation(project(":Form"))
    implementation(project(":KotlinRuntime"))
    implementation(project(":Text"))
    implementation(project(":PlayerInstance"))
    implementation(project(":UtilsOffline"))
    implementation(project(":Timers"))
    api(project(":PlayerInstance"))
    implementation(project(":Location"))
    implementation(project(":Inventory"))
    api(project(":Database"))
    //implementation("com.github.LeonMangler:SuperVanish:6.2.6-4")
    implementation("me.neznamy:tab-api:3.2.1")
}

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
    implementation(project(":Profile"))
    implementation(project(":UtilsOffline"))
    implementation(project(":Timers"))
    implementation(project(":Location"))
    implementation(project(":Inventory"))
    implementation(project(":StaffProfiles"))
    api(project(":Profile"))
    api(project(":Database"))
    api(project(":Inventory"))
    //implementation("com.github.LeonMangler:SuperVanish:6.2.6-4")
    implementation("me.neznamy:tab-api:3.2.1")
}

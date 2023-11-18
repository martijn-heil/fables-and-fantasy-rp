plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:Form"))
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:UtilsOffline"))
    implementation(project(":plugin:technical:Timers"))
    implementation(project(":plugin:core:Profile"))
    implementation(project(":plugin:core:Location"))
    implementation(project(":plugin:core:Inventory"))
    implementation(project(":plugin:core:StaffProfiles"))
    implementation(project(":plugin:core:Time"))
    implementation(project(":plugin:technical:Web"))
    api(project(":plugin:technical:Database"))
    api(project(":plugin:core:Profile"))
    api(project(":plugin:core:Inventory"))
    //implementation("com.github.LeonMangler:SuperVanish:6.2.6-4")
    implementation("me.neznamy:tab-api:3.2.1")
}

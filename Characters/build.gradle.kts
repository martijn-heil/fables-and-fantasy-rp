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
    implementation(project(":Location"))
    implementation(project(":Inventory"))
    api(project(":Database"))
}

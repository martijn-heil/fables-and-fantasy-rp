plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Database"))
    implementation(project(":Timers"))
    implementation(project(":Text"))
    implementation(project(":Form"))
    implementation(project(":Gui"))
    implementation(project(":Chat"))
    implementation(project(":Characters"))
    implementation(project(":WorldGuardInterop"))
    implementation(project(":Discord"))
}

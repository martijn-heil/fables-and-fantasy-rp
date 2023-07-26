plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Profile"))
    implementation(project(":Characters"))
    implementation(project(":Database"))
    implementation(project(":Timers"))
    implementation(project(":Text"))
    implementation(project(":WorldGuardInterop"))
    implementation(project(":Gui"))
    implementation(project(":Form"))
}

plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Database"))
    implementation(project(":Timers"))
    implementation(project(":Text"))
    implementation(project(":WorldGuardInterop"))
}
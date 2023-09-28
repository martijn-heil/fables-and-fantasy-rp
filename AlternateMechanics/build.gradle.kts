plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":WorldGuardInterop"))
    implementation(project(":Characters"))
}

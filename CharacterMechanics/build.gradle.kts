plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Database"))
    implementation(project(":Text"))
    implementation(project(":Characters"))
    implementation(project(":Knockout"))
    implementation(project(":Hacks"))
    implementation(project(":WorldGuardInterop"))
    implementation(project(":Location"))
    implementation(project(":Inventory"))
}

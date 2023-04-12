plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Characters"))
    implementation(project(":Profile"))
    implementation(project(":Location"))
}

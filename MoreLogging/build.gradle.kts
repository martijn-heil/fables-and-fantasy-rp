plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Characters"))
    implementation(project(":DenizenInterop"))
    implementation("com.github.LeonMangler:SuperVanish:6.2.6-4")
}

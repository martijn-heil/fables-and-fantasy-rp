plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Characters"))
}

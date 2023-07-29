plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Form"))
    implementation(project(":Gui"))
    implementation(project(":Economy"))
    implementation(project(":Item"))
    implementation(project(":Characters"))
}

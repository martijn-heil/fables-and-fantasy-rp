plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Database"))
    implementation(project(":Profile"))
    implementation(project(":Characters"))
    implementation(project(":Gui"))
    implementation(project(":Form"))
}

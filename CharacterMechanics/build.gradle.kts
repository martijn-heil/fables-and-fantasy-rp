plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Characters"))
    implementation(project(":CharacterTraits"))
    implementation(project(":Database"))
    implementation(project(":Text"))
}

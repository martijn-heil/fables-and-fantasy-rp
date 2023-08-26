plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Chat"))
    implementation(project(":Characters"))
    implementation(project(":CharacterTraits"))
    implementation(project(":Profile"))
    implementation(project(":Location"))
    implementation(project(":Database"))
    implementation("com.github.Keelar:ExprK:master-SNAPSHOT")
}

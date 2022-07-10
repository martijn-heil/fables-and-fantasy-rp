plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Chat"))
    implementation(project(":Characters"))
    implementation(project(":Database"))
    implementation("com.gitlab.martijn-heil:NinCommands:d133c3f4d4")
}

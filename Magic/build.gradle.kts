plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Math"))
    implementation(project(":Characters"))
    implementation(project(":KotlinRuntime"))
    implementation(project(":DenizenInterop"))
    implementation(project(":Database"))
    implementation(project(":Rolls"))
    implementation(project(":Form"))
    implementation(project(":Chat"))
    implementation(project(":Text"))
    implementation(project(":Gui"))
    implementation(project(":Targeting"))
    implementation("com.gitlab.martijn-heil:NinCommands:1.0-SNAPSHOT")
}

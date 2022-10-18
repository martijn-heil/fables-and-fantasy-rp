plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Database"))
    implementation(project(":Characters"))
    implementation(project(":Text"))
    implementation(project(":DenizenInterop"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
}

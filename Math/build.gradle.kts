plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    //implementation(project(":Characters"))
    implementation("com.gitlab.martijn-heil:NinCommands:1.0-SNAPSHOT")
}

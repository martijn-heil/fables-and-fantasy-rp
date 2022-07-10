plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
}

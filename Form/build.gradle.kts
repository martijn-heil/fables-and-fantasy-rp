plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":PlayerData"))
    implementation(project(":Gui"))
    implementation(project(":KotlinRuntime"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
}
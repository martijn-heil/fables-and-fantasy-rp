plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Database"))
    implementation(project(":PlayerData"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
}

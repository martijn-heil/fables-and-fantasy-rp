plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation("com.gitlab.martijn-heil:NinCommands:-SNAPSHOT")
}

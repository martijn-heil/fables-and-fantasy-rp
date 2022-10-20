plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation("com.gitlab.martijn-heil:NinCommands:1.0-SNAPSHOT") { isChanging = true }
}

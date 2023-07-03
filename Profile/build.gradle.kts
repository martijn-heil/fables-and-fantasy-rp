plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Database"))
    implementation(project(":Timers"))
    implementation(project(":Text"))
    implementation(project(":Form"))
    implementation(project(":Gui"))
    implementation(project(":Web"))
    implementation("com.github.LeonMangler:SuperVanish:6.2.6-4")
}

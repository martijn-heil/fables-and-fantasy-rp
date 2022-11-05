plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Gui"))
    implementation(project(":KotlinRuntime"))
    implementation(project(":Text"))
}

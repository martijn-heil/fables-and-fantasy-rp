plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":KotlinRuntime"))
    implementation(project(":Utils"))
    implementation(project(":Locks"))
    implementation(project(":Database"))
}

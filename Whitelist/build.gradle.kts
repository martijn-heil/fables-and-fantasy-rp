plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":KotlinRuntime"))
    implementation(project(":Utils"))
    implementation(project(":UtilsOffline"))
    implementation(project(":Text"))
    implementation(project(":Chat"))
}

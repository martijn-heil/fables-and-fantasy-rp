plugins {
    `java-library`
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":KotlinRuntime"))
    implementation(project(":Utils"))
    implementation("net.quazar.offlinemanager:api:3.0.7")
}

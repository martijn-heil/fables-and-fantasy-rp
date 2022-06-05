plugins {
    `java-library`
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":KotlinRuntime"))
    implementation("net.flawe.offlinemanager:api:3.0.4-SNAPSHOT")
}

plugins {
    `java-library`
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Utils"))
    implementation("net.quazar.offlinemanager:api:3.0.7")
}

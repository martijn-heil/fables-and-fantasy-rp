plugins {
    `java-library`
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:KotlinRuntime"))
}

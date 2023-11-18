plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:Text"))
}

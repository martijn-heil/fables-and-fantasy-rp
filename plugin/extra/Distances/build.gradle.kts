plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:core:Chat"))
    implementation(project(":plugin:core:Characters"))
}

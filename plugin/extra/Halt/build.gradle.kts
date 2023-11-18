plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:core:Profile"))
    implementation(project(":plugin:core:Location"))
}

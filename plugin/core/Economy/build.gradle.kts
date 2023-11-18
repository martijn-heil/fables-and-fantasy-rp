plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:Form"))
    implementation(project(":plugin:core:Profile"))
    implementation(project(":plugin:core:Characters"))
}

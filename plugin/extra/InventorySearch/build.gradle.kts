plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Form"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:Item"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:core:Economy"))
}

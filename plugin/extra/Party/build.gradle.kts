plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Glowing"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:extra:Targeting"))
}

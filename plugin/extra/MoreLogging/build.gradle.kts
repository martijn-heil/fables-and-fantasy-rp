plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation(project(":plugin:core:Characters"))
    //implementation("com.github.LeonMangler:SuperVanish:6.2.6-4")
}

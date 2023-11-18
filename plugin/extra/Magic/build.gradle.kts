plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Math"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Form"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:Web"))
    implementation(project(":plugin:core:Rolls"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:core:Profile"))
    implementation(project(":plugin:core:Location"))
    implementation(project(":plugin:core:Chat"))
    implementation(project(":plugin:extra:Targeting"))
}

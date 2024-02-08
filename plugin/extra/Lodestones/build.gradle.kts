plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Timers"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Form"))
    implementation(project(":plugin:technical:Gui"))
    implementation(project(":plugin:technical:WorldGuardInterop"))
	implementation(project(":plugin:core:Domain"))
    implementation(project(":plugin:core:Characters"))
}

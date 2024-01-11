plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:Hacks"))
    implementation(project(":plugin:technical:WorldGuardInterop"))
	implementation(project(":plugin:core:Domain"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:core:Location"))
    implementation(project(":plugin:core:Inventory"))
    implementation(project(":plugin:extra:Knockout"))
}

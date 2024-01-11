plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:UtilsOffline"))
    implementation(project(":plugin:technical:Text"))
	implementation(project(":plugin:core:Domain"))
    implementation(project(":plugin:core:Chat"))
}

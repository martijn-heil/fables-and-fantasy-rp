plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
	implementation(project(":plugin:core:Domain"))
    implementation(project(":plugin:core:Characters"))
    implementation(project(":plugin:core:Inventory"))
    implementation(project(":plugin:core:Profile"))
    implementation(project(":plugin:core:Location"))
    implementation(project(":plugin:extra:MoreLogging"))
}

plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Characters"))
    implementation(project(":Inventory"))
    implementation(project(":Profile"))
    implementation(project(":Location"))
    implementation(project(":MoreLogging"))
}

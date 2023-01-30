plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Profile"))
    implementation(project(":Location"))
    implementation(project(":Database"))
}

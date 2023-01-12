plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":PlayerInstance"))
    implementation(project(":Location"))
    implementation(project(":Database"))
}

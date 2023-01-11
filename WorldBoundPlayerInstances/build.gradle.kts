plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":PlayerInstance"))
    implementation(project(":Database"))
}

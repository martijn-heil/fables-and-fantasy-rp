plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Profile"))
    implementation(project(":Database"))
    implementation(project(":WorldBoundProfiles"))
}

plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Database"))
    implementation(project(":plugin:core:WorldBoundProfiles"))
    implementation(project(":plugin:core:Profile"))
}
plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":DenizenInterop"))
    implementation(project(":Profile"))
}
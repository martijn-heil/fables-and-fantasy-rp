plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":DenizenInterop"))
    implementation("dev.kord:kord-core:0.9.0")
    api("dev.kord:kord-core:0.9.0")
}

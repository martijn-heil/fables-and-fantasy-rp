plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:Text"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation(project(":plugin:technical:Web"))
    implementation("dev.kord:kord-core:0.9.0")
    api("dev.kord:kord-core:0.9.0")
}

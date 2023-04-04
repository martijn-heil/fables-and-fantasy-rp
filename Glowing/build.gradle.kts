plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":Utils"))
    implementation(project(":DenizenInterop"))
    implementation("me.neznamy:tab-api:3.2.1")
}

plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:Utils"))
    implementation(project(":plugin:technical:DenizenInterop"))
    implementation("me.neznamy:tab-api:3.2.1")
}

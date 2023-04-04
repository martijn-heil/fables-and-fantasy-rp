plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Text"))
    implementation(project(":Database"))
    implementation(project(":Characters"))
    implementation(project(":Targeting"))
    implementation(project(":Glowing"))
    implementation("me.neznamy:tab-api:3.2.1")
}

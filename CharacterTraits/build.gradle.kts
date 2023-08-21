plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":Utils"))
    implementation(project(":Characters"))
    implementation(project(":Database"))
    implementation(project(":Text"))


    api(project(":Characters"))
}

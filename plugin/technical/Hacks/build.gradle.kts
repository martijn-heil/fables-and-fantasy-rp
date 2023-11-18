plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation("com.github.MilkBowl:VaultAPI:1.7")
    implementation(project(":plugin:core:Characters"))
}

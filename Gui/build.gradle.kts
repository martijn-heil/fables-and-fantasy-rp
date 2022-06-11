plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":KotlinRuntime"))
    shadow("de.themoep:inventorygui:1.4.3-SNAPSHOT")
    api("de.themoep:inventorygui:1.4.3-SNAPSHOT")
}

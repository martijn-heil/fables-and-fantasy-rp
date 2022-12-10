plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":KotlinRuntime"))
    shadow("de.themoep:inventorygui:1.4.3-SNAPSHOT")
    api("de.themoep:inventorygui:1.4.3-SNAPSHOT")
    shadow("net.wesjd:anvilgui:1.6.0-SNAPSHOT")
    api("net.wesjd:anvilgui:1.6.0-SNAPSHOT")
}

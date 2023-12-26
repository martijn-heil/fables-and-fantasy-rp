plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Utils"))
    shadow("de.themoep:inventorygui:1.4.3-SNAPSHOT")
    api("de.themoep:inventorygui:1.4.3-SNAPSHOT")
    shadow("net.wesjd:anvilgui:1.9.2-SNAPSHOT")
    api("net.wesjd:anvilgui:1.9.2-SNAPSHOT")
}

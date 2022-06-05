plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    shadow("de.themoep:inventorygui:1.4.3-SNAPSHOT")
    api("de.themoep:inventorygui:1.4.3-SNAPSHOT")
}

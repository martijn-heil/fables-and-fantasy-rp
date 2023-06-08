plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":KotlinRuntime"))
    implementation(project(":Utils"))
    shadow("org.flywaydb:flyway-core:8.5.12")
    shadow("com.h2database:h2:2.1.212")
    shadow("com.dieselpoint:norm:1.0.5")
    api("org.flywaydb:flyway-core:8.5.12")
    api("com.h2database:h2:2.1.212")
    api("com.dieselpoint:norm:1.0.5")
}

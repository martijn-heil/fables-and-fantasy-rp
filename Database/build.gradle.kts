plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    shadow("org.flywaydb:flyway-core:5.0.3")
    shadow("com.h2database:h2:2.1.210")
}

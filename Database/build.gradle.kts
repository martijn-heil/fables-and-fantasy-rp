plugins {
    id("myproject.java-conventions")
}

dependencies {
    // internal module dependencies
    shadow("org.flywaydb:flyway-core:8.5.12")
    shadow("com.h2database:h2:2.1.212")
}

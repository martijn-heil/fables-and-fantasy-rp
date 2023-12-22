plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:KotlinRuntime"))
    implementation(project(":plugin:technical:Utils"))
	implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    shadow("org.flywaydb:flyway-core:8.5.12")
    shadow("com.h2database:h2:2.1.212")
    api("org.flywaydb:flyway-core:8.5.12")
    api("com.h2database:h2:2.1.212")
}

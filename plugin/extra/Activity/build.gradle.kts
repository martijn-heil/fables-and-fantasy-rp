plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:Utils"))
	implementation(project(":plugin:technical:WorldGuardInterop"))
	implementation(project(":plugin:technical:Database"))
	implementation(project(":plugin:technical:Text"))
	implementation(project(":plugin:core:Domain"))
}

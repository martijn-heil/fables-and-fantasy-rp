plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(project(":plugin:technical:Utils"))
	implementation(project(":plugin:technical:Gui"))
}

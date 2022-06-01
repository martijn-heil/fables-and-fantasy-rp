buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath( "com.vanniktech:gradle-dependency-graph-generator-plugin:0.7.0")
    }
}

apply(plugin = "com.vanniktech.dependency.graph.generator")

rootProject.configure<com.vanniktech.dependency.graph.generator.DependencyGraphGeneratorExtension> {
    generators.create("custom") {
        include = {
            dependency -> !dependency.moduleGroup.startsWith("io.papermc") &&
                !dependency.moduleGroup.startsWith("org.bukkit") &&
                !dependency.moduleGroup.startsWith("org.jetbrains.kotlin")
        }
        children = { true } // Include transitive dependencies.
    }
}

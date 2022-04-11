# Fables & Fantasy Kotlin plugins

## Useful links
- [Latest Spigot Javadocs](https://hub.spigotmc.org/javadocs/spigot/)
- H2 Java SQL Database
    - [Data Types](http://www.h2database.com/html/datatypes.html)
    - [Grammar](http://www.h2database.com/html/grammar.html)
    - [Functions](http://www.h2database.com/html/functions.html)


## Structure
The root project directory contains a number of Gradle subprojects, each one of which will produce a single Spigot plugin.
the `buildSrc` directory is the only exception to this, `buildSrc` is our Gradle 'conventions plugin', which allows us
to deduplicate and centralize Gradle behavioral rules between our different subprojects.

## Building
Running `gradle shadowJar` in the project root will build all subprojects.
You can subsequently find the produced plugin jars at `./*/build/libs/Fables*-all.jar`

On \*nix systems you can run the `deployto.sh` script to copy all plugin jars to a directory somewhere else.
Example: `./deployto.sh /to/my/testserver/plugins/`

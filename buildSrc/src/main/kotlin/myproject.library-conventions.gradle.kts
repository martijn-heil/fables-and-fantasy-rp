// Define Java Library conventions for this organization.
// Projects need to use the organization's Java conventions and publish using Maven Publish

plugins {
	`java-library`
	`maven-publish`
	id("myproject.java-conventions")
}

// Projects have the 'com.example' group by convention
group = "com.example"

publishing {
	publications {
		create<MavenPublication>("library") {
			from(components["java"])
		}
	}
	repositories {
		maven {
			name = "myOrgPrivateRepo"
			url = uri("build/my-repo")
		}
	}
}

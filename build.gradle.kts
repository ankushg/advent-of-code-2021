plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            dependencies {
                implementation("com.github.ajalt.mordant:mordant:2.0.0-beta3")
            }
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

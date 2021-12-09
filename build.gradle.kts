import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

tasks {
    wrapper {
        gradleVersion = "7.3.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
            allWarningsAsErrors = true
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta3")
}


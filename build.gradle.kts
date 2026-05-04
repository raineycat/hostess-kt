plugins {
    kotlin("jvm") version "2.3.10"
}

group = "uk.reddust.hostess"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.logging)
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(25)
}

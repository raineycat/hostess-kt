plugins {
    kotlin("jvm") version "2.3.10"
    application
}

group = "uk.reddust.hostess"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.logging)
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass = "uk.reddust.hostess.MainKt"
}

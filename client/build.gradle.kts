plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.2.1"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val ktorVersion = "2.1.3"

dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
}

compose.desktop {
    application {
        mainClass = "org.jub.kotlin.frontend.MainKt"
    }
}
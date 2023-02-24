plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
}

dependencies {
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
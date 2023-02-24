plugins {
    kotlin("jvm")
    id("application")
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("io.ktor.plugin") version "2.1.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
}

val ktorVersion = "2.1.3"

dependencies {
    implementation(project(":common"))
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
}

application {
    mainClass.set("org.jub.kotlin.ApplicationKt")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

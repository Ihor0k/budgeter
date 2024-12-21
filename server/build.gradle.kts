val exposedVersion: String by project
val postgresVersion: String by project
val microutilsVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
}

group = "ua.ihor0k"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-data-conversion")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("io.github.microutils:kotlin-logging:$microutilsVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(kotlin("test"))

//    implementation("com.h2database:h2:2.3.232")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
    jvmToolchain(21)
}
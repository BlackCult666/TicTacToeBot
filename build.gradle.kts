plugins {
    application
    kotlin("jvm") version "2.0.0"
}

application {
    mainClass = "MainKt"
}

group = "io.casiopea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")

}

dependencies {
    implementation("com.github.AgeOfWar:Telejam:v7.15")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
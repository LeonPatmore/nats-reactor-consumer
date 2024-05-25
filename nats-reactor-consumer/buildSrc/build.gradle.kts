plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.5")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.24")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.9.23")
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.5")
}

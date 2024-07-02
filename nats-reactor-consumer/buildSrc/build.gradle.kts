plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.1")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.0")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.0.0")
    implementation("io.spring.dependency-management:io.spring.dependency-management.gradle.plugin:1.1.5")
}

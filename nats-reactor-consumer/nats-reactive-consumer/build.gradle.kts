import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("nats.kotlin-library")
	`java-test-fixtures`
}

tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

tasks.getByName<Jar>("jar") {
	enabled = true
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	api("io.nats:jnats:2.17.6")
	api("io.projectreactor:reactor-core:3.6.5")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
	implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")

	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	testImplementation("io.mockk:mockk:1.13.11")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")

	testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
	testFixturesImplementation("org.testcontainers:junit-jupiter:1.19.8")
}

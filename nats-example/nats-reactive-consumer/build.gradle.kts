plugins {
    id("nats.kotlin-library")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.nats:jnats:2.17.6")
	implementation("io.projectreactor:reactor-core:3.6.5")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
	implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

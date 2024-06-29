plugins {
    id("nats.kotlin-library")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
	implementation(project(":nats-reactive-consumer"))
}

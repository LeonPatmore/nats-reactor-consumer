plugins {
    id("nats.kotlin-library")
	id("com.google.cloud.tools.jib") version "3.4.3"
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
	implementation(project(":nats-reactive-consumer"))
}

jib {
	to {
		image = "nats-test-consumer"
	}
	from {
		image = "amazoncorretto:21"
	}
}

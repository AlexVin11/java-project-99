import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	java
	checkstyle
	application
	jacoco
	id("io.freefair.lombok") version "8.6"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.github.ben-manes.versions") version "0.49.0"
	id("io.sentry.jvm.gradle") version "5.2.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

application {
	mainClass.set("hexlet.code.app.AppApplication")
}

dependencies {
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	implementation("com.zaxxer:HikariCP:5.0.1")
	implementation("org.postgresql:postgresql:42.7.2")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-config:6.4.2")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.assertj:assertj-core:3.26.3")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
	implementation("net.datafaker:datafaker:2.0.2")
	implementation("org.instancio:instancio-junit:3.3.1")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
	implementation("io.sentry:sentry-opentelemetry-agent:8.2.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	useJUnitPlatform()
	// https://technology.lastminute.com/junit5-kotlin-and-gradle-dsl/
	testLogging {
		exceptionFormat = TestExceptionFormat.FULL
		events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
		// showStackTraces = true
		// showCauses = true
		showStandardStreams = true
	}
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }

sentry {
	// Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
	// This enables source context, allowing you to see your source
	// code as part of your stack traces in Sentry.
	includeSourceContext = true

	org = "vinogradow-corp"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.sentryBundleSourcesJava {
	enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}

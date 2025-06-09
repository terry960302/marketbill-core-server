import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.5"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.serialization") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"

	kotlin("plugin.noarg") version "1.6.21"
	kotlin("plugin.allopen") version "1.6.21"
	id("com.netflix.dgs.codegen") version "5.6.0"
	kotlin("kapt") version "1.7.10"
}

group = "kr.co.marketbill"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

extra["kotlin.version"] = "1.7.0"
extra["graphql-java.version"] = "19.2"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

	// jpa + db conn
	implementation("org.postgresql:postgresql:42.5.4")
	implementation("org.springframework:spring-jdbc:6.0.6")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.vladmihalcea:hibernate-types-52:2.16.0")

	// graphql
//	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release")) // SpringBoot 3.0.0 이상 호환되는 DGS 버전
	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:5.5.1")) // SpringBoot 2.7.5 호환되는 DGS 버전
	implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
	implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

	// actuator
	implementation("org.springframework.boot:spring-boot-starter-actuator:3.0.4")

	// security
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("io.jsonwebtoken:jjwt:0.9.1") // jwt
	implementation("org.mindrot:jbcrypt:0.4") // hash

	// api client
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

	// kotest
	testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
	testImplementation("io.kotest:kotest-assertions-core:5.5.5")
	testImplementation("io.kotest:kotest-property:5.5.5")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

	// queryDSL
	implementation("com.querydsl:querydsl-jpa:5.0.0")
        kapt("com.querydsl:querydsl-apt:5.0.0:jpa")
}

kapt {
        javacOptions {
                option("-J--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
                option("-J--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
        }
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs(
                "--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
                "--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
        )
}

// kotlin no default constructor error 해결용
allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}

noArg {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.MappedSuperclass")
	annotation("javax.persistence.Embeddable")
}


// codegen for graphql DGS
tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
	schemaPaths = mutableListOf("${projectDir}/src/main/resources/schema") // List of directories containing schema files
	packageName = "kr.co.marketbill.marketbillcoreserver" // The package name to use to generate sources
	generateDataTypes = true
	language = "kotlin"
	typeMapping = mutableMapOf(
	)
//	generateClient = true
}
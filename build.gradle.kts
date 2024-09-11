plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.allopen") version "2.0.0"

    id("org.springframework.boot") version "3.2.9"//"2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.barabo"

repositories {
    mavenCentral()
}

dependencies {
    //implementation("org.springframework.boot:spring-boot-starter-logging")

    //implementation("org.springframework.boot:spring-boot-starter-actuator:2.7.18")
    //implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.18")
    implementation("org.springframework.boot:spring-boot-starter-security")
   //implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    /*
    implementation("org.springframework.security:spring-security-web:5.8.13")
    implementation("org.springframework.security:spring-security-core:5.8.13")
    implementation("org.springframework.security:spring-security-ldap:5.8.13")
    implementation("org.springframework.security:spring-security-data:5.8.13")
    implementation("org.springframework.ldap:spring-ldap-core:2.4.1")
*/
    implementation("com.unboundid:unboundid-ldapsdk:6.0.11") //пусто

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.jsonwebtoken:jjwt:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("org.apache.directory.api:api-all:2.1.7")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
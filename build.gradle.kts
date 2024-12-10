plugins {
    java
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
   
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}
//val conf by configurations.creating
dependencies {
    // conf("com.thing:foo:1.0")
    // conf("org.example:bar:1.0")
	 implementation ("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation ("org.postgresql:postgresql:42.6.0")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.postgresql:postgresql")
    runtimeOnly ("org.postgresql:postgresql")
}

// tasks.register("filterDependencies") {
//     val files: FileCollection = conf.incoming.artifactView {
//         componentFilter {
//             when(it) {
//                 is ModuleComponentIdentifier ->
//                     it.group == "com.thing" && it.module == "foo"
//                 else -> false
//             }
//         }
//     }.files

//     doLast {
//         assert(files.map { it.name } == listOf("foo-1.0.jar"))
//     }
// }

tasks.withType<Test> {
    useJUnitPlatform()
}


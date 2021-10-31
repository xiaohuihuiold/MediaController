plugins {
    id("org.jetbrains.intellij") version "1.2.1"
    kotlin("jvm") version "1.5.31"
    java
}

group = "com.xhhold.plugin"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.2.3")
}
tasks {
    patchPluginXml {
        /*changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent())*/
    }
}
tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
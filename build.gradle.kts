plugins {
  application
  java
  val kotlinVersion = "1.4.31"
  kotlin("jvm").version(kotlinVersion)
  kotlin("plugin.serialization").version(kotlinVersion)
  id("com.github.johnrengelman.shadow").version("6.1.0")
}


group = "org.anvilpowered"
version = "0.1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
  maven("https://kotlin.bintray.com/kotlinx")
  maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
  val kotlinxSerializationVersion = "1.1.0"
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$kotlinxSerializationVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
  implementation("org.asynchttpclient:async-http-client:2.12.1")
  implementation("com.lmax:disruptor:3.4.2")
  implementation("com.google.inject:guice:5.0.1")
  implementation("net.dv8tion:JDA:4.2.0_247")
  val slf4jVersion = "2.13.1"
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:$slf4jVersion")
  implementation("org.apache.logging.log4j:log4j-iostreams:$slf4jVersion")
  implementation("org.spongepowered:configurate-hocon:4.0.0")
}

application {
  mainClassName = "org.anvilpowered.falcon.Falcon"
}

tasks.shadowJar {
  archiveName = "Falcon-${version}.jar"
}

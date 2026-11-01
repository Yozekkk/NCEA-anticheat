plugins { `java-library`; jacoco }
tasks.withType<JavaCompile>().configureEach { options.release.set(17); options.encoding = "UTF-8" }
dependencies { testImplementation("org.junit.jupiter:junit-jupiter:5.11.4"); testRuntimeOnly("org.junit.platform:junit-platform-launcher") }
tasks.test { useJUnitPlatform() }

tasks.jacocoTestReport { dependsOn(tasks.test); reports { xml.required.set(true); html.required.set(true) } }
tasks.check { dependsOn(tasks.jacocoTestReport) }

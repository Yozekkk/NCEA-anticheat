plugins { java; jacoco }
sourceSets {
    named("main") { java.setSrcDirs(emptyList<String>()); resources.setSrcDirs(emptyList<String>()) }
    create("paper120") { java.srcDir("src/main/java"); resources.srcDir("src/main/resources") }
    create("paper1201") { java.srcDir("src/main/java"); resources.srcDir("src/main/resources") }
}
dependencies {
    add("paper120CompileOnly", "io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    add("paper120Implementation", project(":ncea-core"))
    add("paper1201CompileOnly", "io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    add("paper1201Implementation", project(":ncea-core"))
    testImplementation(sourceSets["paper1201"].output)
    testImplementation(project(":ncea-core"))
    testImplementation("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.withType<JavaCompile>().configureEach { options.release.set(17); options.encoding = "UTF-8" }
val coreJar = project(":ncea-core").tasks.named<Jar>("jar")
val jar120 = tasks.register<Jar>("jar120") {
    dependsOn("paper120Classes", coreJar)
    archiveFileName.set("NCEA-AntiCheat-1.20.jar")
    destinationDirectory.set(rootProject.layout.projectDirectory.dir("dist"))
    from(sourceSets["paper120"].output)
    from(zipTree(coreJar.get().archiveFile))
}
val jar1201 = tasks.register<Jar>("jar1201") {
    dependsOn("paper1201Classes", coreJar)
    archiveFileName.set("NCEA-AntiCheat-1.20.1.jar")
    destinationDirectory.set(rootProject.layout.projectDirectory.dir("dist"))
    from(sourceSets["paper1201"].output)
    from(zipTree(coreJar.get().archiveFile))
}
tasks.named<Jar>("jar") { enabled = false }
tasks.assemble { dependsOn(jar120, jar1201) }

tasks.test { useJUnitPlatform() }
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(sourceSets["paper1201"].output.classesDirs)
    sourceDirectories.setFrom(files("src/main/java"))
    reports { xml.required.set(true); html.required.set(true) }
}
tasks.check { dependsOn(tasks.jacocoTestReport) }

// Examples: -PbannerStyle=original-3d -PbannerColors=on -PbannerMode=ansi16
//           -PbannerStyle=custom -PbannerCustom=/absolute/path/custom-banner.txt
tasks.register<JavaExec>("previewBanner") {
    dependsOn(":ncea-core:classes")
    classpath = project(":ncea-core").sourceSets["main"].runtimeClasspath
    mainClass.set("com.ncea.anticheat.core.branding.BannerPreview")
    args("--style=" + (findProperty("bannerStyle") ?: "original-3d"),
         "--colors=" + (findProperty("bannerColors") ?: "off"),
         "--mode=" + (findProperty("bannerMode") ?: "ansi16"),
         "--charset=" + (findProperty("bannerCharset") ?: "unicode"),
         "--theme=" + (findProperty("bannerTheme") ?: "ncea-original"),
         "--width=" + (findProperty("bannerWidth") ?: "auto"),
         "--output=" + (findProperty("bannerOutput") ?: layout.buildDirectory.file(if ((findProperty("bannerColors") ?: "off") == "on") "previews/banner.ansi.txt" else "previews/banner.txt").get().asFile.absolutePath),
         "--html=" + (findProperty("bannerHtml") ?: layout.buildDirectory.file("previews/banner.html").get().asFile.absolutePath))
    if (findProperty("bannerCustom") != null) args("--custom=" + findProperty("bannerCustom"))
}

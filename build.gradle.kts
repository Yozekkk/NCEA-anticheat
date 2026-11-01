plugins { base }
allprojects {
    group = "com.ncea.anticheat"
    version = "1.0.0"
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
tasks.named("build") { dependsOn(":ncea-core:build", ":ncea-paper:assemble") }

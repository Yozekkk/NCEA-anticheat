plugins { base }
allprojects {
    group = "com.ncea.anticheat"
    version = "0.1.0-dev"
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
tasks.named("build") { dependsOn(":ncea-core:build", ":ncea-paper:assemble") }

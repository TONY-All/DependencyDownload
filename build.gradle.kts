plugins {
    java
    `maven-publish`
}

version = "2.0.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = "cc.maxmc.denpdendencydownload"
    version = rootProject.version

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                artifactId = "dependencydownload-" + project.name

                pom {
                    name.set("dependencydownload-" + project.name)
                    packaging = "jar"
                    description.set("A library to download dependencies during runtime")
                    url.set("https://github.com/tony-all/DependencyDownload")

                    scm {
                        connection.set("scm:git:https://github.com/tony-all/DependencyDownload.git")
                        developerConnection.set("scm:git:https://github.com/Vankka/DependencyDownload.git")
                        url.set("https://github.com/Vankka/DependencyDownload")
                    }

                    developers {
                        developer {
                            id.set("Vankka")
                        }
                        developer {
                            id.set("TONY_All")
                        }
                    }
                }
            }
        }

        repositories {
            mavenLocal()
            maven("https://repo.vip.maxmc.cc:30443/snapshots") {
                name = "mrepo"
                credentials(PasswordCredentials::class.java)
            }
            maven("https://maven.pkg.github.com/tony-all/dependencydownload") {
                name = "github"
                credentials(PasswordCredentials::class.java)
            }
            maven("https://repo.tabooproject.org/repository/releases/") {
                name = "taboolib"
                credentials(PasswordCredentials::class.java)
            }
        }
    }

}

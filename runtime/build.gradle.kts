dependencies {
    api(project(":common"))
    implementation("me.lucko:jar-relocator:1.6")
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "dev.vankka.dependencydownload.runtime")
    }
}

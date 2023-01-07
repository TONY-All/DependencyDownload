dependencies {
    api(project(":common"))
    implementation("me.lucko:jar-relocator:1.5")
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "dev.vankka.dependencydownload.runtime")
    }
}

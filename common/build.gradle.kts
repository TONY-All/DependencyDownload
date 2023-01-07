dependencies {
    compileOnlyApi("org.jetbrains:annotations:23.0.0")
    compileOnlyApi("com.google.errorprone:error_prone_annotations:2.12.1")
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "dev.vankka.dependencydownload.common")
    }
}

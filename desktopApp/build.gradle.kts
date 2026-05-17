import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.shared)
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Red HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Serialización JSON (sin Gson que es Android)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
}

compose.desktop {
    application {
        mainClass = "com.example.app_sice_multiplataforma.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.app_sice_multiplataforma"
            packageVersion = "1.0.0"
        }
    }
}

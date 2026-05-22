import org.jetbrains.compose.desktop.application.dsl.TargetFormat
// Importante importar esto para definir la versión de Java
import org.gradle.api.JavaVersion

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

// 1. FORZAR A GRADLE A USAR JAVA 17 (O 21) PARA ESTE MÓDULO
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// 2. FORZAR AL COMPILADOR DE KOTLIN A APUNTAR A LA JVM 17
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(projects.shared)
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.sqlite.bundled)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)

    // Red HTTP
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Serialización JSON
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
plugins {
    // Android Gradle Plugin
    id("com.android.application") version "8.9.2" apply false
    // Kotlin Android Plugin
    kotlin("android") version "2.1.20" apply false
    // KSP（用於 Room、Hilt 等 codegen）
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
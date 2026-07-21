pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    // Multi-version toolchain: https://stonecutter.kikugie.dev/
    id("dev.kikugie.stonecutter") version "0.9.6"
    // Handles correct Loom version/variant per Minecraft version automatically
    id("dev.kikugie.loom-back-compat") version "0.4.1"
    // Lets Gradle auto-provision the JDK needed for each Minecraft version.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // 1.21.1 → 1.21–1.21.8  (old input system)
        // 1.21.9 → 1.21.9–1.21.11 (MouseButtonEvent/KeyEvent)
        // 26.1   → 26.1–26.1.2  (unobfuscated, new versioning)
        // 26.2   → 26.2
        versions("1.21.1", "1.21.9", "26.1", "26.2")
        vcsVersion = "1.21.1"
    }
}

rootProject.name = "nickgroups"

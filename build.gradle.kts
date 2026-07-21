plugins {
    id("dev.kikugie.loom-back-compat")
}

// Stonecutter shares this build script across every version node.
// `sc.current` is the node currently being built (e.g. 1.21.1 or 26.1).
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

// Minecraft 26.x+ ships without obfuscation — no mappings file exists.
// All earlier 1.21.x versions still require official Mojang mappings.
val isUnobfuscated = sc.current.version.startsWith("26.")

repositories {
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    if (isUnobfuscated) {
        implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    } else {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("nickgroups") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }

    runConfigs.all {
        // Share the run directory between version nodes.
        runDirectory = rootProject.file("run")
    }
}

java {
    withSourcesJar()

    // 26.x+ requires Java 25; 1.21.x works on Java 21
    val javaVersion = if (isUnobfuscated) JavaVersion.VERSION_25 else JavaVersion.VERSION_21
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    toolchain {
        languageVersion = JavaLanguageVersion.of(if (isUnobfuscated) 25 else 21)
    }
}

// Resolve TOML-backed properties at project scope (inside a task closure `property()`
// resolves against the task, not the project).
val modId = project.property("mod.id") as String
val modName = project.property("mod.name") as String
val modVersion = project.property("mod.version") as String
val mcCompat = project.property("mod.mc_compat") as String

tasks.processResources {
    filteringCharset = "UTF-8"
    val props = mapOf(
        "id" to modId,
        "name" to modName,
        "version" to modVersion,
        "minecraft" to mcCompat,
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
}

// Collect the final jar of each node into build/libs/<mod version>/.
// 26.x+  → no remap step, use plain jar
// 1.21.x → use remapped jar
tasks.register<Copy>("buildAndCollect") {
    group = "build"
    if (isUnobfuscated) {
        from(tasks.named<org.gradle.jvm.tasks.Jar>("jar").flatMap { it.archiveFile })
    } else {
        from(tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar").flatMap { it.archiveFile })
    }
    into(rootProject.layout.buildDirectory.dir("libs/$modVersion"))
    dependsOn("build")
}

tasks.register("printClasspath") {
    doLast {
        val clientCompileClasspath = project.configurations.getByName("clientCompileClasspath")
        println("=== CLIENT CLASSPATH FOR ${project.name} ===")
        clientCompileClasspath.files.forEach { println(it.absolutePath) }
    }
}

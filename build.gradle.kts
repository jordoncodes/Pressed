plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") version "1.7.4"
}

group = "me.onlyjordon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi") {
        name = "placeholderapi"
    }
    maven("https://maven.citizensnpcs.co/repo") {
        name = "citizens"
    }
    maven("https://repo.codemc.io/repository/maven-releases/") {
        name = "codemc"
    }
    maven("https://jitpack.io/") {
        name = "jitpack"
    }
    maven("https://eldonexus.de/repository/maven-public/") {
        name = "eldonexus"
    }
    maven("https://maven.enginehub.org/repo/") {
        name = "worldedit"
    }
    maven("https://repo.auroramc.gg/repository/maven-public/") {
        name = "aurora"
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.6.1")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.6.1")
    implementation("fr.mrmicky:fastboard:2.1.3")
    implementation("com.github.retrooper:packetevents-spigot:2.5.0")
    implementation("de.snowii:mojang-api:1.1.0")
    implementation("dev.triumphteam:triumph-gui:3.1.10")
    compileOnly("com.github.NEZNAMY:TAB-API:5.0.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("gg.auroramc:Aurora:2.0.6")
    compileOnly("gg.auroramc:AuroraLevels:1.6.0")
    compileOnly(files("libs/GadgetsMenu.jar"))
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    build {
        dependsOn("shadowJar")
    }
    shadowJar {
        relocate("dev.jorel.commandapi", "me.onlyjordon.pressed.libs.commandapi")
        relocate("fr.mrmicky.fastboard", "me.onlyjordon.pressed.libs.fastboard")
        relocate("dev.triumphteam.gui", "me.onlyjordon.pressed.libs.gui")
    }
    runServer {
        systemProperty("net.kyori.adventure.text.warnWhenLegacyFormattingDetected", false)
        minecraftVersion("1.21.1")
        downloadPlugins {
//            hangar("ViaVersion", "5.1.0")
//            hangar("ViaBackwards", "5.1.0")
//            hangar("ViaRewind", "4.0.3")
//            hangar("ViaRewindLegacySupport", "1.5.2-SNAPSHOT+33")
            github("Multiverse", "Multiverse-Core", "4.3.13", "multiverse-core-4.3.13.jar")
            github("xtkq-is-not-available", "VoidGen", "v2.2.1", "VoidGen-2.2.1.jar")
        }
    }
//
//    assemble {
//        dependsOn(tasks.reobfJar)
//    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

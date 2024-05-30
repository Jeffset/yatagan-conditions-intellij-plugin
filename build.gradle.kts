import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    java
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlugin)
    alias(libs.plugins.grammarKit)
    alias(libs.plugins.changelog)
}

group = properties("plugin.group")
version = properties("plugin.version").get()

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion = properties("java.version").map(JavaLanguageVersion::of)
    }
}

java {
    toolchain {
        languageVersion = properties("java.version").map(JavaLanguageVersion::of)
    }
}

intellij {
    pluginName = properties("plugin.name")
    version = properties("plugin.build.platformVersion")
    type = properties("plugin.build.platformType")

    plugins = listOf(
        "org.intellij.intelliLang",
        "com.intellij.java",

        // Just for testing, no real dependency
        "org.jetbrains.kotlin",
    )

    instrumentCode = false
}

val sourceMain = layout.projectDirectory.dir("src").dir("main")
val generatedPsiDir = layout.buildDirectory.dir("generated-java")

sourceSets.main {
    java.srcDirs(generatedPsiDir)
}

val localProperties: Provider<Properties> = providers.fileContents(layout.projectDirectory.file("local.properties"))
    .asText.map { text -> text.reader().use { Properties().apply { load(it) } } }

tasks {
    patchPluginXml {
        version = properties("plugin.version")
        sinceBuild = properties("plugin.sinceBuild")
        untilBuild = properties("plugin.untilBuild")
        pluginId = properties("plugin.id")

        pluginDescription = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map { readme ->
            // Take plugin description from README.md
            readme.lineSequence()
                .dropWhile { it.trim() != "<!-- PLUGIN DESCRIPTION -->" }.drop(1)
                .takeWhile { it.trim() != "<!-- END PLUGIN DESCRIPTION -->" }
                .joinToString("\n")
                .let(::markdownToHTML)
        }

        changeNotes = """
            * Implemented more precise completion and condition resolution
            * Fixed a bug in color scheme settings that prevented users from setting method color
            * Added more precise error and warning reporting

            **Full Changelog**: https://github.com/Jeffset/yatagan-conditions-intellij-plugin/compare/v0.1.1-alpha...v0.2.0
        """.trimIndent().let(::markdownToHTML)
    }

    signPlugin {
        certificateChain = environment("SIGN_PLUGIN_CERTIFICATE_CHAIN")
        privateKey = environment("SIGN_PLUGIN_PRIVATE_KEY")
        password = environment("SIGN_PLUGIN_PRIVATE_KEY_PASSWORD")
    }

    publishPlugin {
        token = environment("PLUGIN_PUBLISH_TOKEN")
    }

    val packagePath = "io/github/jeffset/yatagan/intellij/conditions"
    generateParser {
        sourceFile = sourceMain.file("Yce.bnf")
        targetRootOutputDir = generatedPsiDir
        pathToParser = "$packagePath/YceParser.java"
        pathToPsiRoot = "$packagePath/psi"
        purgeOldFiles = true
    }

    generateLexer {
        val generatedPsiDir = generatedPsiDir  // Conf Cache
        sourceFile = sourceMain.file("Yce.flex")
        targetOutputDir = generatedPsiDir.map { gen -> gen.dir(packagePath).dir("lexer") }
        targetFile("YceLexer")
        purgeOldFiles = true
    }

    val preBuild by registering {
        description = "Makes necessary preparations for code editing and building"
        dependsOn(generateParser, generateLexer)
    }

    setupDependencies {
        // Hooked up to IntelliJ "After Gradle Sync".
        dependsOn(preBuild)
    }

    withType<KotlinCompile> {
        dependsOn(preBuild)
    }

    test {
        val intellijDir = environment("INTELLIJ_SOURCES_DIR").getOrNull() ?: localProperties("intellij.dir").getOrNull()
        intellijDir?.let {
            systemProperty("idea.home.path", it)
        }

        doFirst {
            if (intellijDir == null || !File(intellijDir).isDirectory) {
                throw GradleException(
                    "'idea.home.path' is not set to a valid path: $intellijDir, tests will not work properly")
            }
        }
    }

    wrapper {
        gradleVersion = properties("gradle.version").get()
    }
}

fun localProperties(key: String): Provider<String> = localProperties.map { it[key]?.toString() }
fun properties(key: String): Provider<String> = providers.gradleProperty(key)
fun environment(key: String): Provider<String> = providers.environmentVariable(key)

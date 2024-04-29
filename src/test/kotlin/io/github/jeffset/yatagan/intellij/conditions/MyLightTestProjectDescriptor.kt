package io.github.jeffset.yatagan.intellij.conditions

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.LanguageLevelModuleExtension
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.pom.java.AcceptedLanguageLevelsSettings
import com.intellij.pom.java.LanguageLevel
import com.intellij.testFramework.IdeaTestUtil
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor
import com.intellij.testFramework.fixtures.kotlin.withKotlinStdlib

object MyLightTestProjectDescriptor : DefaultLightProjectDescriptor() {
    init {
        withKotlinStdlib()
        withRepositoryLibrary("com.yandex.yatagan:api-public:1.5.0")
    }

    override fun setUpProject(project: Project, handler: SetupHandler) {
        AcceptedLanguageLevelsSettings.allowLevel(project, LANGUAGE_LEVEL)
        super.setUpProject(project, handler)
    }

    override fun getSdk(): Sdk {
        return IdeaTestUtil.getMockJdk(LANGUAGE_LEVEL.toJavaVersion())
    }

    override fun configureModule(module: Module, model: ModifiableRootModel, contentEntry: ContentEntry) {
        model.getModuleExtension(LanguageLevelModuleExtension::class.java).languageLevel = LANGUAGE_LEVEL
        super.configureModule(module, model, contentEntry)
    }

    private val LANGUAGE_LEVEL = LanguageLevel.JDK_1_8
}
import buildparameters.tests.compatibility.*
import org.jetbrains.kotlin.gradle.plugin.*

plugins {
    id("build-parameters")
    id("buildx-multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    allTargets()
    sourceSets {
        commonTest {
            dependencies {
                implementation(projects.cryptographyTests.cryptographyTestUtils)
                implementation(projects.cryptographyTester.cryptographyTesterClient)
                implementation(libs.ktor.utils) //for base64
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }

    val excludedTests = mapOf(
        Step.InMemory to "*.inMemoryTest",
        Step.Generate to "*.generateStep",
        Step.Validate to "*.validateStep",
    ).filterKeys { it != buildParameters.tests.compatibility.step }.values.toTypedArray()
    targets.all {
        if (this is KotlinTargetWithTests<*, *>) testRuns.all {
            filter {
                setExcludePatterns(*excludedTests)
            }
        }
    }
}
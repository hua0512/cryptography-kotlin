import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.*

fun KotlinMultiplatformExtension.jsTargets() {
    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "600s"
                }
            }
        }
        browser {
            testTask {
                useKarma {
                    useConfigDirectory(project.rootDir.resolve("gradle/js/karma"))
                    useChromeHeadless()
                    //useSafari()
                }
            }
        }
    }
}

fun KotlinMultiplatformExtension.darwinTargets() {
    macosX64()
    macosArm64()
}

fun KotlinMultiplatformExtension.allTargets() {
    jvm()
    jsTargets()
    darwinTargets()
    linuxX64()
    mingwX64()

    //will be replaced with hierarchy with kotlin 1.8.20
    sharedSourceSet("nonJvm") { it.platformType != KotlinPlatformType.jvm && it.platformType != KotlinPlatformType.common }
    setupSharedNativeSourceSets()
}

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("file:C:\\Users\\KNS\\StudioProjects\\flow\\flow\\build\\repo")
        }

        maven {
            url = uri("https://maven.pkg.github.com/anastas-makar/DiamondApi")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }

        maven {
            url = uri("https://maven.pkg.github.com/anastas-makar/AuthApi")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }

    }
}

rootProject.name = "Todos"
include(":app")
 
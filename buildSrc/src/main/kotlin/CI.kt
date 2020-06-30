object CI {

    private val githubRunNumber = System.getenv("GITHUB_RUN_NUMBER")

    private val snapshotVersion = when (githubRunNumber) {
        null -> "LOCAL"
        else -> "GITHUB.${CI.githubRunNumber}-SNAPSHOT"
    }

    private val releaseVersion = System.getenv("RELEASE_VERSION")

    val version = releaseVersion ?: snapshotVersion
}

package movie.rating

import io.ktor.client.HttpClient

internal class RatingProviderRottenTomatoes(
    override val client: HttpClient,
    override val provider: LinkProvider
) : AbstractRatingProvider() {

    override fun getRating(content: String): Byte {
        val scoreBoard = score.find(content)?.value ?: throw ResultNotFoundException()
        return scores.findAll(scoreBoard)
            .flatMap { it.groupValues.drop(1) }
            .mapNotNull { it.toByteOrNull() }
            .maxByOrNull { it }
            ?: throw ResultNotFoundException()
    }

    companion object {

        private val score =
            Regex("<score-board[\\n\\r\\S ]+?>")
        private val scores =
            Regex("(?<=audiencescore|tomatometerscore)=\"(\\d+)\"")

    }

}
package movie.rating

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import movie.rating.internal.LazyHttpClient
import movie.rating.model.ListResponse
import movie.rating.model.SearchData

class MetadataProviderTMDB(
    private val client: LazyHttpClient
) : MetadataProvider {

    override suspend fun get(descriptor: MovieDescriptor): MovieMetadata? {
        val data = client.getOrCreate().search(descriptor) ?: return null
        return MovieMetadata(
            rating = (data.rating * 10).toInt().toByte(),
            posterImageUrl = data.posterPath?.let(TMDB::image).orEmpty(),
            overlayImageUrl = data.backdropPath?.let(TMDB::image).orEmpty()
        )
    }

    private suspend fun HttpClient.search(descriptor: MovieDescriptor): SearchData? {
        val result = get(TMDB.url("/search/movie")) {
            url.parameters.apply {
                append("query", descriptor.name)
                append("include_adult", "false")
                append("primary_release_year", descriptor.year.toString())
            }
        }
        return result.body<ListResponse<SearchData>>().results.firstOrNull()
    }

}
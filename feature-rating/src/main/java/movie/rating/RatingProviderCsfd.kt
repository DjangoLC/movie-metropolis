package movie.rating

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLParameter

class RatingProviderCsfd(
    private val client: HttpClient
) : RatingProvider {

    override suspend fun getRating(descriptor: MovieDescriptor): Byte {
        val link = getDetailLink(descriptor.name, descriptor.year)
        return getRating(link)
    }

    private suspend fun getDetailLink(name: String, year: Int): String {
        val query = name.encodeURLParameter()
        val response = client.get("https://www.csfd.cz/hledat/?q=$query")
        val body = response.bodyAsText()
        for (row in row.findAll(body)) {
            if (!row.value.contains(year.toString())) continue
            val title = title.find(row.value) ?: continue
            val correlationFactor = title.groupValues[2] correlate name
            if (correlationFactor >= .8f) {
                return "https://www.csfd.cz" + title.groupValues[1]
            }
        }
        throw ResultNotFoundException()
    }

    private suspend fun getRating(link: String): Byte {
        val response = client.get(link)
        val body = response.bodyAsText()
        val result = rating.find(body) ?: throw ResultNotFoundException()
        return result.groupValues[1].toByte()
    }

    companion object {

        private val row =
            Regex("<article[\\s\\S]+?>[\\s\\S]+?<\\/article>")

        private val title =
            Regex("<a[ \\S]+?href=\"(.*?)\"[ \\S]+?class=\".*?film-title-name.*?\">([\\w .:&\\-]+?)</a>")

        private val rating =
            Regex("(?:class=\"film-rating-average\"[\\s\\S]*?>)\\s+?(\\d+)%\\s+?<\\/")

    }

}
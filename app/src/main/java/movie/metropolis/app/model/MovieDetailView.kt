package movie.metropolis.app.model

import androidx.compose.runtime.*
import movie.core.model.MovieDetail

@Immutable
interface MovieDetailView {
    val id: String
    val name: String
    val nameOriginal: String
    val releasedAt: String
    val duration: String
    val countryOfOrigin: String
    val cast: List<String>
    val directors: List<String>
    val description: String
    val availableFrom: String
    val poster: ImageView?
    val trailer: VideoView?
    val rating: String?

    fun base(): MovieDetail
}
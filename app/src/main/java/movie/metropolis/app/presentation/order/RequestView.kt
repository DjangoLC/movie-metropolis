package movie.metropolis.app.presentation.order

import androidx.compose.runtime.*

@Stable
data class RequestView(
    val url: String,
    val headers: Map<String, String>
)
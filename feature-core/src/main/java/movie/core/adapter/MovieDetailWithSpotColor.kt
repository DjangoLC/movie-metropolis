package movie.core.adapter

import movie.core.model.MovieDetail

internal data class MovieDetailWithSpotColor(
    private val origin: MovieDetail,
    override val spotColor: Int
) : MovieDetail by origin
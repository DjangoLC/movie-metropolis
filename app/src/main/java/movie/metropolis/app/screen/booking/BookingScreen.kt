package movie.metropolis.app.screen.booking

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import movie.metropolis.app.model.BookingView
import movie.metropolis.app.screen.Loadable
import movie.metropolis.app.screen.detail.plus
import movie.metropolis.app.screen.home.HomeScreenLayout
import movie.metropolis.app.screen.onLoading
import movie.metropolis.app.screen.onSuccess
import movie.metropolis.app.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    padding: PaddingValues,
    state: LazyListState,
    profileIcon: @Composable () -> Unit,
    onMovieClick: (String) -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val active by viewModel.active.collectAsState()
    val expired by viewModel.expired.collectAsState()
    HomeScreenLayout(
        profileIcon = profileIcon,
        title = { Text("Tickets") }
    ) { innerPadding, behavior ->
        BookingScreenContent(
            padding = innerPadding + padding,
            behavior = behavior,
            active = active,
            expired = expired,
            onRefreshClick = viewModel::refresh,
            onMovieClick = onMovieClick,
            state = state
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BookingScreenContent(
    padding: PaddingValues,
    active: Loadable<List<BookingView.Active>>,
    expired: Loadable<List<BookingView.Expired>>,
    behavior: TopAppBarScrollBehavior,
    onRefreshClick: () -> Unit = {},
    onMovieClick: (String) -> Unit = {},
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = Modifier
            .nestedScroll(behavior.nestedScrollConnection)
            .fillMaxSize(),
        contentPadding = padding + PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = !active.isLoading || !expired.isLoading,
        state = state
    ) {
        active.onSuccess { view ->
            item("ticket-cta") {
                Button(
                    onClick = onRefreshClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Find new tickets")
                }
            }
            items(view, BookingView::id) {
                var isVisible by rememberSaveable { mutableStateOf(false) }
                BookingItemActive(
                    modifier = Modifier.animateItemPlacement(),
                    name = it.name,
                    cinema = it.cinema.name,
                    date = it.date,
                    time = it.time,
                    poster = it.movie.poster,
                    duration = it.movie.duration,
                    onClick = { isVisible = true }
                )
                BookingTicketDialog(
                    code = it.id,
                    poster = it.movie.poster?.url.orEmpty(),
                    hall = it.hall,
                    seats = it.seats.map { it.row to it.seat },
                    time = it.time,
                    name = it.name,
                    isVisible = isVisible,
                    onVisibilityChanged = { isVisible = it }
                )
            }
            item(key = "divider") { Divider(Modifier.padding(16.dp)) }
        }.onLoading {
            items(1) {
                BookingItemActive()
            }
            item(key = "divider") { Divider(Modifier.padding(16.dp)) }
        }
        expired.onSuccess { view ->
            items(view, BookingView::id) {
                BookingItemExpired(
                    modifier = Modifier.animateItemPlacement(),
                    name = it.name,
                    date = it.date,
                    time = it.time,
                    poster = it.movie.poster,
                    duration = it.movie.duration,
                    onClick = { onMovieClick(it.movie.id) }
                )
            }
        }.onLoading {
            items(2) {
                BookingItemExpired()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun Preview() {
    Theme {
        HomeScreenLayout(
            profileIcon = {},
            title = {}
        ) { padding, behavior ->
            BookingScreenContent(
                padding = padding,
                active = Loadable.loading(),
                expired = Loadable.loading(),
                behavior = behavior
            )
        }
    }
}
package com.codelytical.muvyz.search.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codelytical.muvyz.R
import com.codelytical.muvyz.core.domain.model.Film
import com.codelytical.muvyz.core.presentation.components.StandardToolbar
import com.codelytical.muvyz.core.presentation.theme.MoviewTheme
import com.codelytical.muvyz.core.util.Constants
import com.codelytical.muvyz.core.util.createImageUrl
import com.codelytical.muvyz.genre.domain.model.Genre
import com.codelytical.muvyz.search.domain.model.Search
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.FilmDetailsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import retrofit2.HttpException
import java.io.IOException

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Destination<RootGraph>
@Composable
fun SearchScreen(
    navigator: DestinationsNavigator,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchUiState by viewModel.searchUiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = Unit) {
        viewModel.getMoviesGenres()
        viewModel.getTvSeriesGenres()
    }

    SearchScreenContent(
        state = searchUiState,
        onEvent = { event ->
            when (event) {
                is SearchUiEvents.SearchFilm -> {
                    viewModel.searchAll(event.searchTerm)
                    keyboardController?.hide()
                }

                is SearchUiEvents.SearchTermChanged -> {
                    viewModel.updateSearchTerm(event.value)
                    viewModel.searchAll(event.value)
                }

                is SearchUiEvents.OpenFilmDetails -> {
                    keyboardController?.hide()
                    val search = event.search
                    if (search != null) {
                        navigator.navigate(
                            FilmDetailsScreenDestination(
                                film = search.toFilm()
                            )
                        )
                    }
                }

                SearchUiEvents.ClearSearchTerm -> {
                    viewModel.clearSearch()
                }
            }
        }
    )
}

@Composable
fun SearchScreenContent(
    state: SearchUiState,
    onEvent: (SearchUiEvents) -> Unit,
) {
    val searchResult = state.searchResult.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            StandardToolbar(
                title = {
                    Text(
                        text = stringResource(R.string.search_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            SearchBar(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .height(56.dp),
                onEvent = onEvent,
                state = state,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(searchResult.itemCount) { index ->
                    val search = searchResult[index]
                    SearchItem(
                        search = search,
                        state = state,
                        onClick = {
                            onEvent(SearchUiEvents.OpenFilmDetails(search))
                        }
                    )
                }

                searchResult.loadState.let { loadState ->
                    when {
                        loadState.refresh is LoadState.Loading && state.searchTerm.isNotEmpty() -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillParentMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }

                        loadState.refresh is LoadState.NotLoading && searchResult.itemCount < 1 -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillParentMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .size(250.dp),
                                        painter = painterResource(id = R.drawable.ic_empty_cuate),
                                        contentDescription = null
                                    )
                                }
                            }
                        }


                        loadState.refresh is LoadState.Error -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillParentMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        text = when ((loadState.refresh as LoadState.Error).error) {
                                            is HttpException -> {
                                                "Oops, something went wrong!"
                                            }

                                            is IOException -> {
                                                "Couldn't reach server, check your internet connection!"
                                            }

                                            else -> {
                                                "Unknown error occurred"
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(16.dp),
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "An error occurred",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    onEvent: (SearchUiEvents) -> Unit,
) {
    TextField(
        modifier = modifier,
        value = state.searchTerm,
        onValueChange = {
            onEvent(SearchUiEvents.SearchTermChanged(it))
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = MaterialTheme.colorScheme.onBackground.copy(.5f),
            )
        },
        shape = MaterialTheme.shapes.large,
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
        ),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
        ),
        maxLines = 1,
        singleLine = true,
        trailingIcon = {
            if (state.searchTerm.isNotEmpty()) {
                IconButton(onClick = {
                    onEvent(SearchUiEvents.ClearSearchTerm)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = MaterialTheme.colorScheme.onBackground.copy(.5f),
                        contentDescription = null
                    )
                }
            }
        },
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    state: SearchUiState,
    search: Search?,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${Constants.IMAGE_BASE_UR}/${search?.posterPath}")
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.35f),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(.7f),
                    text = (search?.name?.trim() ?: search?.originalName?.trim()
                    ?: search?.originalTitle?.trim()
                    ?: "---"),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = search?.overview ?: "No description",
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )

                (search?.firstAirDate ?: search?.releaseDate)?.let {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {

                    val moviesGenres = state.moviesGenres
                    val seriesGenres = state.tvSeriesGenres

                    var searchGenres: List<Genre> = emptyList()
                    if (search?.mediaType == "tv") {
                        searchGenres = seriesGenres.filter {
                            search.genreIds?.contains(it.id)!!
                        }
                    }
                    if (search?.mediaType == "movie") {
                        searchGenres = moviesGenres.filter {
                            search.genreIds?.contains(it.id)!!
                        }
                    }

                    for (genre in searchGenres) {
                        GenreComponent(
                            modifier = Modifier
                                .wrapContentSize(),
                            genre = genre,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GenreComponent(
    modifier: Modifier = Modifier,
    genre: Genre,
) {
    Card(
        modifier = modifier
            .border(
                width = .5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(50)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                ),
            text = genre.name,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Normal,
            fontSize = 8.sp
        )
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    MoviewTheme {
        SearchScreenContent(
            state = SearchUiState(),
            onEvent = {}
        )
    }
}

fun Search.toFilm() = Film(
    id = id,
    type = mediaType,
    image = posterPath?.createImageUrl() ?: "",
    category = "",
    name = name ?: originalName ?: originalTitle ?: "",
    rating = voteAverage?.toFloat() ?: 0f,
    releaseDate = firstAirDate ?: releaseDate ?: "",
    overview = overview ?: ""
)

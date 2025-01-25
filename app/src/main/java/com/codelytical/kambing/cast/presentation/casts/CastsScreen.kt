package com.codelytical.muvyz.cast.presentation.casts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codelytical.muvyz.R
import com.codelytical.muvyz.cast.domain.model.Cast
import com.codelytical.muvyz.cast.domain.model.Credits
import com.codelytical.muvyz.core.presentation.components.StandardToolbar
import com.codelytical.muvyz.core.presentation.theme.MoviewTheme
import com.codelytical.muvyz.core.util.Constants
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CastDetailsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun CastsScreen(
    credits: Credits,
    navigator: DestinationsNavigator
) {
    CastsScreenContent(
        credits = credits,
        onEvent = { event ->
            when (event) {
                is CastsUiEvents.NavigateBack -> {
                    navigator.popBackStack()
                }

                is CastsUiEvents.NavigateToCastDetails -> {
                    navigator.navigate(
                        CastDetailsScreenDestination(event.cast)
                    )
                }
            }
        },
    )
}

@Composable
fun CastsScreenContent(
    credits: Credits,
    onEvent: (CastsUiEvents) -> Unit,
) {
    Scaffold(
        topBar = {
            StandardToolbar(
                onBackArrowClicked = {
                    onEvent(CastsUiEvents.NavigateBack)
                },
                title = {
                    Text(
                        text = stringResource(R.string.casts),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                showBackArrow = true
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = credits.cast,
                key = { cast -> cast.id }
            ) { cast ->
                CastItem(
                    imageSize = 170.dp,
                    castImageUrl = "${Constants.IMAGE_BASE_UR}/${cast.profilePath}",
                    castName = cast.name,
                    onClick = {
                        onEvent(CastsUiEvents.NavigateToCastDetails(cast))
                    }
                )
            }
        }
    }
}

@Composable
fun CastItem(
    modifier: Modifier = Modifier,
    imageSize: Dp,
    castName: String,
    castImageUrl: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable {
            onClick()
        },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(castImageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(id = R.drawable.ic_placeholder),
            contentDescription = castName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
        )

        Text(
            text = castName,
            color = MaterialTheme.colorScheme.background.copy(.5f),
            fontWeight = FontWeight.ExtraLight,
            fontSize = 11.sp
        )
    }
}

@Preview
@Composable
fun CastsScreenPreview() {
    MoviewTheme {
        CastsScreenContent(
            credits = Credits(
                cast = castListTestData,
                id = 1,
            ),
            onEvent = {}
        )
    }
}

val castListTestData = listOf(
    Cast(
        adult = false,
        castId = 123,
        character = "John Doe",
        creditId = "abc123",
        gender = 2,
        id = 456,
        knownForDepartment = "Acting",
        name = "Jane Doe",
        order = 1,
        originalName = "Jane Doe",
        popularity = 7.5,
        profilePath = "/profile/jane_doe.jpg"
    ),
    Cast(
        adult = true,
        castId = 456,
        character = "Alice",
        creditId = "xyz789",
        gender = 1,
        id = 789,
        knownForDepartment = "Acting",
        name = "Bob",
        order = 2,
        originalName = "Bob",
        popularity = 8.2,
        profilePath = "/profile/bob.jpg"
    ),
    Cast(
        adult = false,
        castId = 789,
        character = "Eve",
        creditId = "def456",
        gender = 2,
        id = 1011,
        knownForDepartment = "Acting",
        name = "David",
        order = 10,
        originalName = "David",
        popularity = 6.8,
        profilePath = "/profile/david.jpg"
    ),
    Cast(
        adult = true,
        castId = 1011,
        character = "John",
        creditId = "sdsd",
        gender = 2,
        id = 1013,
        knownForDepartment = "Acting",
        name = "David",
        order = 10,
        originalName = "David",
        popularity = 6.8,
        profilePath = "/profile/david.jpg"
    ),
    Cast(
        adult = true,
        castId = 1011,
        character = "John",
        creditId = "sdsd",
        gender = 2,
        id = 10103,
        knownForDepartment = "Acting",
        name = "David",
        order = 10,
        originalName = "David",
        popularity = 6.8,
        profilePath = "/profile/david.jpg"
    ),
    Cast(
        adult = true,
        castId = 1011,
        character = "John",
        creditId = "sdsd",
        gender = 2,
        id = 13013,
        knownForDepartment = "Acting",
        name = "David",
        order = 10,
        originalName = "David",
        popularity = 6.8,
        profilePath = "/profile/david.jpg"
    ),
    Cast(
        adult = true,
        castId = 1011,
        character = "John",
        creditId = "sdsd",
        gender = 2,
        id = 10130,
        knownForDepartment = "Acting",
        name = "David",
        order = 10,
        originalName = "David",
        popularity = 6.8,
        profilePath = "/profile/david.jpg"
    ),
    Cast(
        adult = true,
        castId = 1011,
        character = "John",
        creditId = "sdsd",
        gender = 2,
        id = 1093,
        knownForDepartment = "Acting",
        name = "David",
        order = 10,
        originalName = "David",
        popularity = 6.8,
        profilePath = "/profile/david.jpg"
    ),
)

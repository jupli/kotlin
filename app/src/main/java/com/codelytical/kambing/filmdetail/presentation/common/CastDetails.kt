package com.codelytical.muvyz.filmdetail.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codelytical.muvyz.R
import com.codelytical.muvyz.cast.presentation.casts.CastItem
import com.codelytical.muvyz.core.util.Constants
import com.codelytical.muvyz.filmdetail.presentation.FilmDetailsUiEvents
import com.codelytical.muvyz.filmdetail.presentation.FilmDetailsUiState

@Composable
fun CastDetails(
    state: FilmDetailsUiState,
    modifier: Modifier = Modifier,
    onEvent: (FilmDetailsUiEvents) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (state.isLoadingCasts) {
            CircularProgressIndicator()
        }

        if (state.errorCasts != null) {
            Text(text = state.errorCasts)
        }

        if (state.isLoadingCasts.not() && state.errorCasts == null && state.credits != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.cast),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                )

                Row(
                    modifier = Modifier.clickable {
                        onEvent(FilmDetailsUiEvents.NavigateToCastsScreen(state.credits))
                    },
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.view_all),
                        fontWeight = FontWeight.ExtraLight,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_chevron_right),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }


            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = {
                    items(state.credits.cast.take(4)) { cast ->
                        CastItem(
                            imageSize = 90.dp,
                            castImageUrl = "${Constants.IMAGE_BASE_UR}/${cast.profilePath}",
                            castName = cast.name,
                            onClick = {
                                onEvent(FilmDetailsUiEvents.NavigateToCastDetails(cast))
                            }
                        )
                    }
                })
        }
    }
}

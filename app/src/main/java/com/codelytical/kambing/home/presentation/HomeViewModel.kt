
package com.codelytical.muvyz.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.codelytical.muvyz.core.util.Resource
import com.codelytical.muvyz.genre.domain.model.Genre
import com.codelytical.muvyz.genre.domain.usecase.GetMovieGenresUseCase
import com.codelytical.muvyz.genre.domain.usecase.GetTvSeriesGenresUseCase
import com.codelytical.muvyz.home.data.repository.MoviesRepository
import com.codelytical.muvyz.home.data.repository.TvSeriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val seriesRepository: TvSeriesRepository,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTvSeriesGenresUseCase: GetTvSeriesGenresUseCase,
) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        getTrendingMovies(null)
        getNowPayingMovies(null)
        getUpcomingMovies(null)
        getTopRatedMovies(null)
        getPopularMovies(null)
        getPopularTvSeries(null)
        getMoviesGenres()

        getAiringTodayTvSeries(null)
        getTrendingTvSeries(null)
        getOnTheAirTvSeries(null)
        getTopRatedTvSeries(null)
        getOnTheAirTvSeries(null)
        getSeriesGenres()
    }

    /**
     * Movies
     */
    fun getTrendingMovies(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                trendingMovies = if (genreId != null) {
                    moviesRepository.getTrendingMoviesThisWeek().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    moviesRepository.getTrendingMoviesThisWeek().cachedIn(viewModelScope)
                }
            )
        }
    }


    fun getUpcomingMovies(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                upcomingMovies = if (genreId != null) {
                    moviesRepository.getUpcomingMovies().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    moviesRepository.getUpcomingMovies().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getTopRatedMovies(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                topRatedMovies = if (genreId != null) {
                    moviesRepository.getTopRatedMovies().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    moviesRepository.getTopRatedMovies().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getNowPayingMovies(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                nowPlayingMovies = if (genreId != null) {
                    moviesRepository.getNowPlayingMovies().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    moviesRepository.getNowPlayingMovies().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getPopularMovies(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                popularMovies = if (genreId != null) {
                    moviesRepository.getPopularMovies().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    moviesRepository.getPopularMovies().cachedIn(viewModelScope)
                }
            )
        }
    }

    private fun getMoviesGenres() {
        viewModelScope.launch {
            when (val result = getMovieGenresUseCase()) {
                is Resource.Success -> {
                    _homeUiState.update {
                        it.copy(
                            moviesGenres = result.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    //loadingError.value = result.message.toString()
                }

                else -> {}
            }
        }
    }

    /**
     * Tv Series
     */
    fun getTrendingTvSeries(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                trendingTvSeries = if (genreId != null) {
                    seriesRepository.getTrendingThisWeekTvSeries().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    seriesRepository.getTrendingThisWeekTvSeries().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getOnTheAirTvSeries(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                onAirTvSeries = if (genreId != null) {
                    seriesRepository.getOnTheAirTvSeries().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    seriesRepository.getOnTheAirTvSeries().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getTopRatedTvSeries(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                topRatedTvSeries = if (genreId != null) {
                    seriesRepository.getTopRatedTvSeries().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    seriesRepository.getTopRatedTvSeries().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getAiringTodayTvSeries(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                airingTodayTvSeries = if (genreId != null) {
                    seriesRepository.getAiringTodayTvSeries().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    seriesRepository.getAiringTodayTvSeries().cachedIn(viewModelScope)
                }
            )
        }
    }

    fun getPopularTvSeries(genreId: Int?) {
        _homeUiState.update {
            it.copy(
                popularTvSeries = if (genreId != null) {
                    seriesRepository.getPopularTvSeries().map { pagingData ->
                        pagingData.filter {
                            it.genreIds.contains(genreId)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    seriesRepository.getPopularTvSeries().cachedIn(viewModelScope)
                }
            )
        }
    }

    private fun getSeriesGenres() {
        viewModelScope.launch {
            when (val result = getTvSeriesGenresUseCase()) {
                is Resource.Success -> {
                    _homeUiState.update {
                        it.copy(
                            tvSeriesGenres = result.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    //loadingError.value = result.message.toString()
                }

                else -> {}
            }
        }
    }

    fun setGenre(genre: Genre?) {
        _homeUiState.update {
            it.copy(
                selectedGenre = genre
            )
        }
    }

    fun setSelectedOption(item: String) {
        _homeUiState.update {
            it.copy(
                selectedFilmOption = item
            )
        }
    }

    fun refreshAllData() {
        getSeriesGenres()
        getMoviesGenres()
        getTrendingMovies(homeUiState.value.selectedGenre?.id)
        getNowPayingMovies(homeUiState.value.selectedGenre?.id)
        getUpcomingMovies(homeUiState.value.selectedGenre?.id)
        getTopRatedMovies(homeUiState.value.selectedGenre?.id)
        getPopularMovies(homeUiState.value.selectedGenre?.id)
        getPopularTvSeries(homeUiState.value.selectedGenre?.id)
        getAiringTodayTvSeries(homeUiState.value.selectedGenre?.id)
        getTrendingTvSeries(homeUiState.value.selectedGenre?.id)
        getOnTheAirTvSeries(homeUiState.value.selectedGenre?.id)
        getTopRatedTvSeries(homeUiState.value.selectedGenre?.id)
        getOnTheAirTvSeries(homeUiState.value.selectedGenre?.id)
    }
}

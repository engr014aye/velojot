package com.ahmadini.velojot.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ahmadini.velojot.domain.model.NoteType
import com.ahmadini.velojot.domain.repository.NoteRepository
import com.ahmadini.velojot.domain.usecase.DeleteNoteUseCase
import com.ahmadini.velojot.domain.usecase.GetNoteByIdUseCase
import com.ahmadini.velojot.domain.usecase.GetNotesUseCase
import com.ahmadini.velojot.domain.usecase.SaveNoteUseCase
import com.ahmadini.velojot.domain.usecase.ToggleArchiveUseCase
import com.ahmadini.velojot.domain.usecase.TogglePinUseCase
import com.ahmadini.velojot.domain.usecase.UpdateNoteColorUseCase
import com.ahmadini.velojot.presentation.screens.note_detail.NoteDetailScreen
import com.ahmadini.velojot.presentation.screens.note_detail.NoteDetailViewModel
import com.ahmadini.velojot.presentation.screens.notes_list.NotesListScreen
import com.ahmadini.velojot.presentation.screens.notes_list.NotesListViewModel

@Composable
fun VeloJotNavGraph(
    navController: NavHostController,
    repository: NoteRepository,
    modifier: Modifier = Modifier
) {
    // Instantiate UseCases
    val getNotesUseCase = GetNotesUseCase(repository)
    val getNoteByIdUseCase = GetNoteByIdUseCase(repository)
    val saveNoteUseCase = SaveNoteUseCase(repository)
    val deleteNoteUseCase = DeleteNoteUseCase(repository)
    val togglePinUseCase = TogglePinUseCase(repository)
    val toggleArchiveUseCase = ToggleArchiveUseCase(repository)
    val updateNoteColorUseCase = UpdateNoteColorUseCase(repository)

    NavHost(
        navController = navController,
        startDestination = Screen.NotesList.route,
        modifier = modifier
    ) {
        // Notes List Screen
        composable(
            route = Screen.NotesList.route,
            enterTransition = { fadeIn(animationSpec = tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            val listViewModel: NotesListViewModel = viewModel(
                factory = NotesListViewModel.Factory(
                    getNotesUseCase,
                    deleteNoteUseCase,
                    togglePinUseCase,
                    toggleArchiveUseCase,
                    updateNoteColorUseCase
                )
            )

            NotesListScreen(
                viewModel = listViewModel,
                onNavigateToCreateNote = { type ->
                    navController.navigate(Screen.NoteDetail.createRoute(0L, type))
                },
                onNavigateToNoteDetail = { noteId ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId))
                }
            )
        }

        // Note Detail Screen
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = 0L
                },
                navArgument("initialType") {
                    type = NavType.StringType
                    defaultValue = NoteType.TEXT.name
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(250)
                ) + fadeOut(animationSpec = tween(250))
            }
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            val typeStr = backStackEntry.arguments?.getString("initialType") ?: NoteType.TEXT.name
            val initialType = try {
                NoteType.valueOf(typeStr)
            } catch (e: Exception) {
                NoteType.TEXT
            }

            val detailViewModel: NoteDetailViewModel = viewModel(
                key = "note_detail_$noteId",
                factory = NoteDetailViewModel.Factory(
                    noteId = noteId,
                    initialType = initialType,
                    getNoteByIdUseCase = getNoteByIdUseCase,
                    saveNoteUseCase = saveNoteUseCase,
                    deleteNoteUseCase = deleteNoteUseCase
                )
            )

            NoteDetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

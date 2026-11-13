package com.yh.fridgesoksok.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yh.fridgesoksok.presentation.camera.CameraScreen
import com.yh.fridgesoksok.presentation.edit_food.EditFoodScreen
import com.yh.fridgesoksok.presentation.home.HomeScreen
import com.yh.fridgesoksok.presentation.login.LoginScreen
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.onboarding.OnboardingScreen
import com.yh.fridgesoksok.presentation.recipe_detail.RecipeDetailScreen
import com.yh.fridgesoksok.presentation.theme.FridgeSokSokTheme
import com.yh.fridgesoksok.presentation.upload.UploadScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 프로세스가 종료된 뒤 복원되는 경우 저장된 화면 상태를 버리고 온보딩부터 다시 시작
        // (토큰 확인 전에 홈 화면이 복원되어 API 를 호출하는 것을 방지)
        val restoredAfterProcessDeath = savedInstanceState != null && !hasBeenCreatedInProcess
        hasBeenCreatedInProcess = true
        super.onCreate(if (restoredAfterProcessDeath) null else savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeSokSokTheme {
                val navController = rememberNavController()
                // 전역 뷰모델 (Activity-Scoped ViewModel)
                val activityViewModel: SharedViewModel = hiltViewModel()

                // 세션 만료 시 백스택을 비우고 로그인 화면으로 이동
                LaunchedEffect(Unit) {
                    activityViewModel.sessionExpired.collect {
                        if (navController.currentDestination?.route == Screen.LoginScreen.route) return@collect
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.OnboardingScreen.route,
                    enterTransition = { enterTransitionFor(targetState) },
                    exitTransition = { exitTransitionFor(targetState) },
                    popEnterTransition = { popEnterTransitionFor(initialState) },
                    popExitTransition = { popExitTransitionFor(initialState) }
                ) {
                    composable(Screen.OnboardingScreen.route) {
                        OnboardingScreen(navController)
                    }

                    composable(Screen.LoginScreen.route) {
                        LoginScreen(navController)
                    }

                    composable(Screen.HomeScreen.route) {
                        HomeScreen(navController, activityViewModel)
                    }

                    composable(Screen.RecipeDetailScreen.route) {
                        RecipeDetailScreen(navController, activityViewModel)
                    }

                    composable(Screen.UploadScreen.route) {
                        UploadScreen(navController, activityViewModel)
                    }

                    composable(Screen.EditFoodScreen.route) {
                        EditFoodScreen(navController)
                    }

                    composable(
                        Screen.CameraScreen.route,
                        enterTransition = { slideInVertically(initialOffsetY = { it }) + fadeIn() },
                        exitTransition = { fadeOut() },
                    ) {
                        CameraScreen(navController, activityViewModel)
                    }
                }
            }
        }
    }

    companion object {
        // 상세/편집처럼 "들어갔다 나오는" 화면: 오른쪽에서 밀려 들어오고 뒤로 가면 오른쪽으로 나감
        // 그 외(온보딩/로그인/홈 전환)는 짧은 페이드
        private val pushRoutes = setOf(
            Screen.RecipeDetailScreen.route,
            Screen.UploadScreen.route,
            Screen.EditFoodScreen.route
        )
        private const val TRANSITION_MS = 280
        private const val FADE_MS = 200

        private fun isPush(entry: NavBackStackEntry) = entry.destination.route in pushRoutes
        private fun <T> slideSpec() = tween<T>(TRANSITION_MS, easing = FastOutSlowInEasing)

        private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransitionFor(target: NavBackStackEntry): EnterTransition =
            if (isPush(target)) slideInHorizontally(slideSpec()) { it / 3 } + fadeIn(tween(TRANSITION_MS))
            else fadeIn(tween(FADE_MS))

        private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransitionFor(target: NavBackStackEntry): ExitTransition =
            if (isPush(target)) slideOutHorizontally(slideSpec()) { -it / 8 } + fadeOut(tween(TRANSITION_MS))
            else fadeOut(tween(FADE_MS))

        private fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransitionFor(initial: NavBackStackEntry): EnterTransition =
            if (isPush(initial)) slideInHorizontally(slideSpec()) { -it / 8 } + fadeIn(tween(TRANSITION_MS))
            else fadeIn(tween(FADE_MS))

        private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransitionFor(initial: NavBackStackEntry): ExitTransition =
            if (isPush(initial)) slideOutHorizontally(slideSpec()) { it / 3 } + fadeOut(tween(TRANSITION_MS))
            else fadeOut(tween(FADE_MS))

        // 현재 프로세스에서 MainActivity 가 생성된 적이 있는지 (구성 변경과 프로세스 복원을 구분)
        private var hasBeenCreatedInProcess = false
    }
}

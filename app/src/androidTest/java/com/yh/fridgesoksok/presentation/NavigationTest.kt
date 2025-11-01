package com.yh.fridgesoksok.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
        }
    }

    @Test
    fun navController_시작_경로는_OnboardingScreen이어야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            // 시작 경로 설정
            navController.setCurrentDestination(Screen.OnboardingScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.OnboardingScreen.route, currentRoute)
    }

    @Test
    fun navController_LoginScreen으로_이동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.OnboardingScreen.route)
        }

        // When
        composeTestRule.runOnIdle {
            navController.navigate(Screen.LoginScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.LoginScreen.route, currentRoute)
    }

    @Test
    fun navController_HomeScreen으로_이동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.LoginScreen.route)
        }

        // When
        composeTestRule.runOnIdle {
            navController.navigate(Screen.HomeScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.HomeScreen.route, currentRoute)
    }

    @Test
    fun navController_CameraScreen으로_이동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.HomeScreen.route)
        }

        // When
        composeTestRule.runOnIdle {
            navController.navigate(Screen.CameraScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.CameraScreen.route, currentRoute)
    }

    @Test
    fun navController_UploadScreen으로_이동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.CameraScreen.route)
        }

        // When
        composeTestRule.runOnIdle {
            navController.navigate(Screen.UploadScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.UploadScreen.route, currentRoute)
    }

    @Test
    fun navController_EditFoodScreen으로_이동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.UploadScreen.route)
        }

        // When
        composeTestRule.runOnIdle {
            navController.navigate(Screen.EditFoodScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.EditFoodScreen.route, currentRoute)
    }

    @Test
    fun navController_RecipeDetailScreen으로_이동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.HomeScreen.route)
        }

        // When
        composeTestRule.runOnIdle {
            navController.navigate(Screen.RecipeDetailScreen.route)
        }

        // Then
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.RecipeDetailScreen.route, currentRoute)
    }

    @Test
    fun navController_뒤로가기가_올바르게_작동해야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.OnboardingScreen.route)
        }

        // Given - 여러 화면 네비게이션
        composeTestRule.runOnIdle {
            navController.navigate(Screen.LoginScreen.route)
            navController.navigate(Screen.HomeScreen.route)
        }

        // When - 뒤로가기
        composeTestRule.runOnIdle {
            navController.popBackStack()
        }

        // Then - LoginScreen으로 돌아가야 함
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        assertEquals(Screen.LoginScreen.route, currentRoute)
    }

    @Test
    fun navController_BackStack이_올바르게_관리되어야_한다() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            navController.setCurrentDestination(Screen.OnboardingScreen.route)
        }

        // Given
        composeTestRule.runOnIdle {
            navController.navigate(Screen.LoginScreen.route)
            navController.navigate(Screen.HomeScreen.route)
        }

        // Then - BackStack에 3개의 화면이 있어야 함
        assertEquals(3, navController.backQueue.size)
    }

    @Test
    fun bottomNavItems_3개의_탭이_있어야_한다() {
        // Then
        assertEquals(3, Screen.bottomNavItems.size)
        assertEquals(Screen.FridgeTab, Screen.bottomNavItems[0])
        assertEquals(Screen.RecipeTab, Screen.bottomNavItems[1])
        assertEquals(Screen.AccountTab, Screen.bottomNavItems[2])
    }
}

package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun HomeBottomBar(
    mode: HomeUiMode,
    currentRoute: String,
    homeNavController: NavHostController,
    onClickGenerateRecipe: () -> Unit = {}
) {
    Surface(
        Modifier
            .fillMaxWidth()
            .height(64.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            .windowInsetsPadding(WindowInsets.navigationBars),
        tonalElevation = 6.dp,
        shadowElevation = 6.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            currentRoute == Screen.FridgeTab.route && mode == HomeUiMode.DEFAULT -> BottomNavigationBar(currentRoute, homeNavController)
            currentRoute == Screen.FridgeTab.route && mode == HomeUiMode.RECIPE_SELECT -> GenerateRecipeBar(onClickGenerateRecipe)
            currentRoute == Screen.RecipeTab.route -> BottomNavigationBar(currentRoute, homeNavController)
            currentRoute == Screen.AccountTab.route -> BottomNavigationBar(currentRoute, homeNavController)
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentRoute: String,
    homeNavController: NavHostController
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            Column(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                    .clickable {
                        if (!selected) {
                            homeNavController.navigate(screen.route) {
                                popUpTo(homeNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                screen.selectedIcon?.let {
                    Image(
                        painter = painterResource(if (selected) screen.selectedIcon else screen.unselectedIcon!!),
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )
                }
                screen.label?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected) CustomGreyColor7 else CustomGreyColor5
                    )
                }
            }
        }
    }
}

@Composable
private fun GenerateRecipeBar(
    onClickGenerateRecipe: () -> Unit
) {
    Button(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = { onClickGenerateRecipe() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "레시피 생성하기",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = CustomGreyColor1
        )
    }
}
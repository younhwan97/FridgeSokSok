package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.Screen
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    mode: HomeUiMode,
    currentRoute: String,
    onClickAiRecipeBtn: () -> Unit,
    onClickNavigationBtn: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselectAll: () -> Unit
) {
    when {
        // 냉장고 TAB + 레시피 모드
        currentRoute == Screen.FridgeTab.route && mode == HomeUiMode.RECIPE_SELECT -> {
            TopAppBar(
                title = {
                    Text(
                        text = "AI 레시피 추천",
                        style = MaterialTheme.typography.headlineMedium,
                        color = CustomGreyColor7
                    )
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            modifier = Modifier.width(64.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CustomGreyColor5
                            ),
                            contentPadding = PaddingValues(0.dp),
                            onClick = onClickDeselectAll,
                        ) {
                            Text(
                                text = "전체 해제",
                                style = MaterialTheme.typography.bodySmall,
                                color = CustomGreyColor1
                            )
                        }

                        Button(
                            modifier = Modifier.width(64.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            contentPadding = PaddingValues(0.dp),
                            onClick = onClickSelectAll,
                        ) {
                            Text(
                                text = "전체 선택",
                                style = MaterialTheme.typography.bodySmall,
                                color = CustomGreyColor1
                            )
                        }
                    }
                },
                navigationIcon = {
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { onClickNavigationBtn() },
                        painter = painterResource(R.drawable.back),
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        // 냉장고 TAB
        currentRoute == Screen.FridgeTab.route -> {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo02),
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )
                },
                actions = {
                    Image(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { onClickAiRecipeBtn() },
                        painter = painterResource(R.drawable.ai),
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        // 레시피 TAB
        currentRoute == Screen.RecipeTab.route -> {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(R.drawable.logo02),
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        // 계정 TAB
        currentRoute == Screen.AccountTab.route -> {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "계정",
                        style = MaterialTheme.typography.headlineMedium,
                        color = CustomGreyColor7
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}

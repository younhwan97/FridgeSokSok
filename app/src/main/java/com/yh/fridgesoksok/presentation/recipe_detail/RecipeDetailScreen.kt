package com.yh.fridgesoksok.presentation.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.common.util.rememberActionCooldown
import com.yh.fridgesoksok.presentation.recipe_detail.comp.RecipeContent
import com.yh.fridgesoksok.presentation.recipe_detail.comp.RecipeHeader
import com.yh.fridgesoksok.presentation.recipe_detail.comp.RecipeImage
import com.yh.fridgesoksok.presentation.recipe_detail.comp.RecipeIngredientSection

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    sharedViewModel: com.yh.fridgesoksok.presentation.SharedViewModel
) {
    // SharedViewModel에서 선택된 레시피 가져오기
    val recipe by sharedViewModel.selectedRecipe.collectAsState()

    // 중복처리 제어
    val (canTrigger, triggerCooldown) = rememberActionCooldown()

    recipe?.let { currentRecipe ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item { RecipeImage(currentRecipe.recipeImageUrl) }
                item { Spacer(Modifier.height(8.dp)) }
                item { RecipeHeader(currentRecipe.recipeName, currentRecipe.createdAt) }
                item { HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 1.dp, MaterialTheme.colorScheme.surfaceVariant) }
                item { RecipeIngredientSection(currentRecipe.ingredients, currentRecipe.ingredientTypes) }
                item { HorizontalDivider(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 1.dp, MaterialTheme.colorScheme.surfaceVariant) }
                item { RecipeContent(currentRecipe.recipeContent) }
            }

            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp)
                    .padding(top = 16.dp)
                    .size(28.dp)
                    .align(Alignment.TopStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .zIndex(1f),
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(4.dp)
                        .clickable(enabled = canTrigger) {
                            triggerCooldown()
                            navController.popBackStack()
                        },
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "레시피를 읽어올 수 없습니다 :(",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
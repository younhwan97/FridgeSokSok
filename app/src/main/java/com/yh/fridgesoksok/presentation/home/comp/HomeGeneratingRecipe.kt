package com.yh.fridgesoksok.presentation.home.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yh.fridgesoksok.presentation.RecipeGenerationState
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1

@Composable
fun HomeGeneratingRecipe(
    recipeGenerateState: RecipeGenerationState
) {
    if (recipeGenerateState == RecipeGenerationState.Loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
                .background(Color.Black.copy(alpha = 0.66f))
                .pointerInput(Unit) { /* Consume all touch input */ }
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("recipe_loading.json"))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LottieAnimation(
                    modifier = Modifier.size(256.dp),
                    composition = composition,
                    progress = { progress }
                )

                Text(
                    text = "레시피를 생성중입니다!!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CustomGreyColor1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
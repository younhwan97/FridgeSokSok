package com.yh.fridgesoksok.presentation.recipe_detail.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3

@Composable
fun RecipeImage(
    recipeImageUrl: String
) {
    CoilImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        imageModel = { recipeImageUrl },
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        ),
        loading = {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(1.dp, CustomGreyColor3)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        failure = {
            Image(
                painter = painterResource(R.drawable.basic_food_image),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        },
    )
}
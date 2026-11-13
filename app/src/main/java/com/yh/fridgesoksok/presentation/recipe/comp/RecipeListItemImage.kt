package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.common.comp.shimmer

private const val IMAGE_CROSSFADE_MS = 250

@Composable
fun RecipeListItemImage(imageUrl: String) {
    val context = LocalContext.current
    // 디코딩 크기는 Coil 이 실제 표시 크기(100x120dp → 기기 px)에 맞춤
    // (기존 100x120 px 고정은 고해상도 기기에서 흐릿하게 보였음)
    val imageRequest = remember(imageUrl, context) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(IMAGE_CROSSFADE_MS)
            .build()
    }

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(width = 100.dp, height = 120.dp)
            .clip(RoundedCornerShape(12.dp)),
        loading = {
            Box(modifier = Modifier.fillMaxSize().shimmer())
        },
        error = {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.basic_food_image),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    )
}

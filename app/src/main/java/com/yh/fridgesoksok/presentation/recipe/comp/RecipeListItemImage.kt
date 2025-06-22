package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.common.ResizeTransformation

@Composable
fun RecipeListItemImage(imageUrl: String) {
    val context = LocalContext.current

    val imageRequest = remember(imageUrl, context) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(false)
            .size(100, 120)
            .allowHardware(false)
            .transformations(ResizeTransformation(100, 120))
            .build()
    }

    val painter = rememberAsyncImagePainter(model = imageRequest)
    val state = painter.state

    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 120.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            is AsyncImagePainter.State.Error -> {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.basic_food_image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
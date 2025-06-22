package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.common.extension.DateFormatter
import com.yh.fridgesoksok.presentation.common.ResizeTransformation
import com.yh.fridgesoksok.presentation.common.extension.toFormattedDate
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor6
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import kotlinx.coroutines.delay
import kotlin.text.Typography.nbsp

@Composable
fun RecipeListItem(
    item: RecipeModel,
    isBeingDeleted: Boolean,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteAnimationEnd: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    val formattedDate = remember(item.createdAt) { item.createdAt.toFormattedDate(DateFormatter.yyyyMMddDot) }

    LaunchedEffect(isBeingDeleted) {
        if (isBeingDeleted) visible = false
    }

    LaunchedEffect(visible) {
        if (!visible && isBeingDeleted) {
            delay(300)
            onDeleteAnimationEnd()
        }
    }

    AnimatedVisibility(
        visible = visible,
        exit = shrinkVertically(tween(300)) + fadeOut(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 4.dp, horizontal = 16.dp)
                .padding(bottom = 10.dp)
                .background(Color.White)
                .clickable { onClick() },
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RecipeListItemImage(item.recipeImageUrl)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(0.7f),
                        text = item.recipeName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomGreyColor7,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        modifier = Modifier.size(24.dp),
                        onClick = {
                            onDeleteClick()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                        )
                    }
                }

                Text(
                    text = item.recipeContent,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    fontWeight = FontWeight.Normal,
                    color = CustomGreyColor6,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "생성날짜$nbsp$nbsp${formattedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomGreyColor5,
                    maxLines = 1
                )
            }
        }
    }
}
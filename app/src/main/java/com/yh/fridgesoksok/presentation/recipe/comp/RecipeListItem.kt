package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.yh.fridgesoksok.presentation.model.RecipeModel
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor6
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun RecipeListItem(
    item: RecipeModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            CoilImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { item.recipeImageUrl },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                ),
                loading = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(1.dp, CustomGreyColor3, RoundedCornerShape(12.dp))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                failure = {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(1.dp, CustomGreyColor3, RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "X",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CustomGreyColor3,
                            textAlign = TextAlign.Center
                        )
                    }
                },
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = item.recipeName,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomGreyColor7,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.recipeContent,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                fontWeight = FontWeight.Normal,
                color = CustomGreyColor6,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = item.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = CustomGreyColor5,
                maxLines = 1
            )
        }
    }
}
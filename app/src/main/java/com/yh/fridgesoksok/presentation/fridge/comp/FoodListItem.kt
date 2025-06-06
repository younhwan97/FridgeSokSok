package com.yh.fridgesoksok.presentation.fridge.comp

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.common.DateFormatter
import com.yh.fridgesoksok.presentation.home.HomeUiMode
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor6
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import java.time.LocalDate
import java.time.Period

@Composable
fun FoodListItem(
    modifier: Modifier = Modifier,
    mode: HomeUiMode,
    food: FoodModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val imageResId = remember(food.categoryId) {
        Type.entries.firstOrNull { it.id == food.categoryId }?.icon ?: R.drawable.health
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (mode == HomeUiMode.RECIPE_SELECT && isSelected)
                    Modifier.border((1.5).dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                else Modifier
            )
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(8.dp)
            .animateContentSize(animationSpec = tween(400))
    ) {
        HeaderRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            mode = mode,
            isSelected = isSelected,
            dDay = food.dDay
        )

        Spacer(modifier = Modifier.height(10.dp))

        FoodImage(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            imageRes = imageResId
        )

        FoodName(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(top = 4.dp),
            name = food.itemName
        )

        if (mode == HomeUiMode.DEFAULT) {
            Spacer(modifier = Modifier.height(10.dp))

            FoodCountAdjuster(
                food = food,
                onClickPlus = onClickPlus,
                onClickMinus = onClickMinus
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        FoodExpiryDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            expiryDate = food.expiryDate
        )
    }
}

@Composable
private fun HeaderRow(
    modifier: Modifier = Modifier,
    mode: HomeUiMode,
    isSelected: Boolean,
    dDay: Long,
) {
    val badgeBackgroundColor = when {
        dDay < 0 -> CustomGreyColor6
        dDay in 0..2 -> MaterialTheme.colorScheme.error
        dDay in 3..7 -> Color(0xFFFFA940)
        else -> CustomGreyColor3
    }

    val badgeTextColor = when {
        dDay < 0 -> Color.White
        dDay in 0..2 -> Color.White
        dDay in 3..7 -> Color.Black
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .background(badgeBackgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (dDay < 0) "D+${-dDay}" else "D-$dDay",
                style = MaterialTheme.typography.bodySmall,
                color = badgeTextColor
            )
        }

        if (mode == HomeUiMode.RECIPE_SELECT) {
            Image(
                painter = painterResource(if (isSelected) R.drawable.checked else R.drawable.unchecked),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun FoodImage(
    modifier: Modifier = Modifier,
    imageRes: Int
) {
    Image(
        modifier = modifier,
        painter = painterResource(imageRes),
        contentDescription = null,
        contentScale = ContentScale.None
    )
}

@Composable
private fun FoodName(
    modifier: Modifier = Modifier,
    name: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = CustomGreyColor7,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FoodCountAdjuster(
    food: FoodModel,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onClickMinus(food) }, modifier = Modifier.size(24.dp)) {
            Icon(
                painter = painterResource(R.drawable.minus_primary),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Text(
            text = food.count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = CustomGreyColor7
        )
        IconButton(onClick = { onClickPlus(food) }, modifier = Modifier.size(24.dp)) {
            Icon(
                painter = painterResource(R.drawable.plus_primary),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun FoodExpiryDate(
    modifier: Modifier = Modifier,
    expiryDate: String
) {
    val formattedDate = try {
        LocalDate.parse(expiryDate, DateFormatter.yyyyMMdd).format(DateFormatter.yyyyMMddDot)
    } catch (e: Exception) {
        expiryDate
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "소비기한",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = CustomGreyColor5
        )
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Normal,
            color = CustomGreyColor5
        )
    }
}

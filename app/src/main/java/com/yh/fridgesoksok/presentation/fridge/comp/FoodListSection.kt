package com.yh.fridgesoksok.presentation.fridge.comp

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.fridge.FridgeState
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor2
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor5
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor6
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
fun FoodListSection(
    foods: List<FoodModel>,
    fridgeState: FridgeState,
    searchQuery: String,
    selectedType: Type,
    onClickCard: (FoodModel) -> Unit,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.outline)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    painter = painterResource(id = R.drawable.lighting),
                    contentScale = ContentScale.None,
                    contentDescription = null,
                )

                Crossfade(
                    modifier = Modifier.fillMaxSize(),
                    targetState = fridgeState
                ) { state ->
                    if (state is FridgeState.Success) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    foods
                                        .filter { it.itemName.contains(searchQuery, ignoreCase = true) }
                                        .filter { selectedType == Type.All || it.categoryId == selectedType.id }
                                        .chunked(2)
                                ) { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        rowItems.forEach { food ->
                                            FoodContent(
                                                modifier = Modifier.weight(1f),
                                                food = food,
                                                period = Period.between(
                                                    LocalDate.now(),
                                                    LocalDate.parse(
                                                        food.expiryDate,
                                                        DateTimeFormatter.ofPattern("yyyyMMdd")
                                                    )
                                                ),
                                                onClick = { onClickCard(food) },
                                                onClickMinus = onClickMinus,
                                                onClickPlus = onClickPlus,
                                            )
                                        }

                                        // 짝수 개가 아닐 때 빈 칸 채우기
                                        if (rowItems.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodContent(
    modifier: Modifier = Modifier,
    food: FoodModel,
    period: Period,
    onClick: () -> Unit,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit,
) {
    val dDay = period.toTotalMonths() * 30 + period.days
    val interactionSource = remember { MutableInteractionSource() }

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    onClick = onClick
                )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            badgeBackgroundColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (dDay < 0) "D+${-dDay}" else "D-$dDay",
                        style = MaterialTheme.typography.bodySmall,
                        color = badgeTextColor
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    painter = painterResource(Type.entries.firstOrNull { it.id == food.categoryId }?.icon ?: R.drawable.health),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = food.itemName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomGreyColor7,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

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
            IconButton(
                onClick = { onClickMinus(food) },
                modifier = Modifier.size(24.dp)
            ) {
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
            IconButton(
                onClick = { onClickPlus(food) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.plus_primary),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "소비기한",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = CustomGreyColor5
            )
            Text(
                text = food.expiryDate,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = CustomGreyColor5
            )
        }
    }
}
package com.yh.fridgesoksok.presentation.food_list

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.theme.CustomBlackTextColor
import com.yh.fridgesoksok.presentation.theme.CustomLightGrayBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomLightGrayTextColor
import com.yh.fridgesoksok.presentation.theme.CustomLightPrimaryColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor
import com.yh.fridgesoksok.presentation.theme.CustomRedColor
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    modifier: Modifier = Modifier,
    viewModel: FoodListViewModel = hiltViewModel()
) {
    val summaryFoods by viewModel.foodList.collectAsState()
    val currentDate = LocalDate.now()
    val inputDateFormat = DateTimeFormatter.ofPattern(/* pattern = */ "yyyyMMdd")

    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        CustomTextField(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(
                    color = Color(0xFFEAF5FF),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFFF5F5F7))
                    .drawBehind {
                        val strokeWidth = 8.dp.toPx()
                        val halfStroke = strokeWidth / 2

                        // top
                        drawLine(
                            color = Color(0xFFE7E8EF),
                            start = Offset(0f, halfStroke),
                            end = Offset(size.width, halfStroke),
                            strokeWidth = strokeWidth
                        )

                        // left
                        drawLine(
                            color = Color(0xFFE7E8EF),
                            start = Offset(halfStroke, 0f),
                            end = Offset(halfStroke, size.height),
                            strokeWidth = strokeWidth
                        )

                        // right
                        drawLine(
                            color = Color(0xFFE7E8EF),
                            start = Offset(size.width - halfStroke, 0f),
                            end = Offset(size.width - halfStroke, size.height),
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    items(summaryFoods.size) { idx ->
                        val period = Period.between(
                            currentDate,
                            LocalDate.parse(summaryFoods[idx].endDt, inputDateFormat)
                        )
                        FoodCard(food = summaryFoods[idx], period = period)
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.tmp_light_image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-8).dp) // 살짝 떠있는 느낌
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        value = value,
        onValueChange = onValueChange,
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {}),
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CustomPrimaryColor,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = "검색할 내용을 입력해주세요!",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun FoodCard(
    food: FoodModel,
    period: Period,
    modifier: Modifier = Modifier
) {
    val dDay = period.toTotalMonths() * 30 + period.days
    val dDayText = when {
        dDay < 0 -> "D+${-dDay}"
        else -> "D-$dDay"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // D-Day 태그
        Box(
            modifier = Modifier
                .background(
                    if (dDay < 0) CustomRedColor else CustomLightPrimaryColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = dDayText, color = Color.White, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 아이콘
        Image(
            painter = painterResource(mapFoodTypeToIcon(food.type)),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 음식 이름
        Text(
            text = food.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 수량 조절
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = null)
            Text(
                text = food.count.toString(),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "소비기한 ${food.endDt}",
            fontSize = 11.sp,
            color = CustomLightGrayTextColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

fun mapFoodTypeToIcon(type: Int): Int =
    when (type) {
        0 -> R.drawable.tmp_ingredients
        1 -> R.drawable.tmp_beverages
        2 -> R.drawable.tmp_bakery
        3 -> R.drawable.tmp_canned
        4 -> R.drawable.tmp_sauces
        5 -> R.drawable.tmp_daily
        6 -> R.drawable.tmp_frozen
        7 -> R.drawable.tmp_health
        else -> R.drawable.tmp_health
    }
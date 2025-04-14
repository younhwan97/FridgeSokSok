package com.yh.fridgesoksok.presentation.food_list

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.theme.CustomBlackTextColor
import com.yh.fridgesoksok.presentation.theme.CustomLightGrayBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomLightGrayTextColor
import com.yh.fridgesoksok.presentation.theme.CustomLightPrimaryColor
import com.yh.fridgesoksok.presentation.theme.CustomRedColor
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    viewModel: FoodListViewModel = hiltViewModel()
) {
    val summaryFoods by viewModel.foodList.collectAsState()
    val groupedFoods = summaryFoods.groupBy { it.endDt }


    val currentDate = LocalDate.now()
    val inputDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    val outputDateFormat = DateTimeFormatter.ofPattern("yy.MM.dd")

    // View
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        groupedFoods.forEach { (endDt, foodsForDate) ->
            val endDate = LocalDate.parse(endDt, inputDateFormat)
            val formattedEndDate = endDate.format(outputDateFormat)
            val period = Period.between(currentDate, endDate)

            item {
                DateHeader(formattedEndDate = formattedEndDate, period = period)
            }

            items(foodsForDate) { food ->
                val itemPeriod =
                    Period.between(currentDate, LocalDate.parse(food.endDt, inputDateFormat))
                Food(food, itemPeriod)
            }
        }
    }
}

@Composable
private fun DateHeader(formattedEndDate: String, period: Period) {
    val dDay = period.toTotalMonths() * 30 + period.days
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = formattedEndDate,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp
        )

        Text(
            text = when {
                dDay < 0 -> "  유통기한이 지났어요!!"
                dDay < 3 -> "  임박!"
                else -> ""
            },
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            color = CustomRedColor
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun Food(
    food: FoodModel,
    period: Period
) {
    val dDay = period.toTotalMonths() * 30 + period.days

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { Unit }
            .height(84.dp)
            .background(color = CustomLightGrayBackGroundColor, shape = RoundedCornerShape(4.dp))
            .padding(vertical = 8.dp, horizontal = 8.dp)
        //.alpha(if (period.toTotalMonths() * 30 + period.days < 0) 0.5f else 1f)
    ) {
        Image(
            painter = painterResource(R.drawable.test_food_image),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(56.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomBlackTextColor,
                fontWeight = FontWeight(500)
            )

            Text(
                text = "구매일자: " + food.startDt,
                style = MaterialTheme.typography.bodySmall,
                color = CustomLightGrayTextColor
            )
        }

        Text(
            text = if (dDay < 0) "D+${-dDay}" else "D-$dDay",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = if (period.toTotalMonths() * 30 + period.days < 5) CustomLightPrimaryColor else CustomLightPrimaryColor,
            fontWeight = FontWeight(500)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}
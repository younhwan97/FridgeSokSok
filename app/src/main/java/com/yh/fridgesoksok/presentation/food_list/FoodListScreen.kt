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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.SummaryFoodModel
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
    // State
    val summaryFoods by viewModel.summaryFood.collectAsState()
    val savedSummaryFoods = rememberSaveable { mutableStateOf(summaryFoods) }

    // Variable/Value
    var lastDate ="00000000"
    val currentDate = LocalDate.now()
    val inputDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    val outputDateFormat = DateTimeFormatter.ofPattern("yy.MM.dd")

    val groupedFoods = summaryFoods.groupBy { it.endDt }



    // View
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {

        groupedFoods.forEach { (endDt, foodsForDate) ->
            val endDate = LocalDate.parse(endDt, inputDateFormat)
            val formattedEndDate = endDate.format(outputDateFormat)
            val period = Period.between(currentDate, endDate)

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = formattedEndDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp
                    )

                    Text(
                        text = when {
                            period.toTotalMonths() * 30 + period.days < 0 -> "  유통기한이 지났어요!!"
                            period.toTotalMonths() * 30 + period.days < 3 -> "  임박!"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = CustomRedColor,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(foodsForDate) { food ->
                val itemPeriod = Period.between(currentDate, LocalDate.parse(food.endDt, inputDateFormat))
                Food(food, itemPeriod)
            }
        }


//        itemsIndexed(items = savedSummaryFoods.value) { index, food ->
//            // 종료 날짜
//            val endDate = LocalDate.parse(food.endDt, inputDateFormat)
//            val formattedEndDate = endDate.format(outputDateFormat)
//            // 현재날짜와 종료날짜의 날짜차이 계산
//            val period = Period.between(currentDate, endDate)
//
//            // 날짜가 바뀌었을 때
//            if (lastDate != food.endDt) {
//                lastDate = food.endDt
//
//                if (index != 0){
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                ) {
//                    Text(
//                        text = formattedEndDate,
//                        style = MaterialTheme.typography.bodyMedium,
//                        fontSize = 14.sp
//                    )
//
//                    Text(
//                        text = if (period.toTotalMonths() * 30 + period.days < 0) "  유통기한이 지났어요!!" else if (period.toTotalMonths() * 30 + period.days < 3) "  임박!" else "",
//                        style = MaterialTheme.typography.bodyMedium,
//                        fontSize = 14.sp,
//                        color = CustomRedColor,
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(4.dp))
//            }
//
//            // 음식 데이터
//            Food(food, period)
//        }
    }
}

@Composable
fun Food(
    summaryFood: SummaryFoodModel,
    period: Period
) {
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
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(56.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column (
            modifier = Modifier.fillMaxHeight().weight(1f),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = summaryFood.name,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomBlackTextColor,
                fontWeight = FontWeight(500)
            )

            Text(
                text = "구매일자: " + summaryFood.startDt,
                style = MaterialTheme.typography.bodySmall,
                color = CustomLightGrayTextColor
            )
        }

        Text(
            text = if (period.toTotalMonths() * 30 + period.days < 0) "D+${-(period.toTotalMonths() * 30 + period.days)}" else "D-${period.toTotalMonths() * 30 + period.days}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = if (period.toTotalMonths() * 30 + period.days< 5) CustomLightPrimaryColor else CustomLightPrimaryColor,
            fontWeight = FontWeight(500)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}
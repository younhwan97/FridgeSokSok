package com.yh.fridgesoksok.presentation.food_list

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.SummaryFoodModel
import com.yh.fridgesoksok.presentation.theme.CustomBoxBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomGrayTextColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor
import com.yh.fridgesoksok.presentation.theme.CustomSecondaryColor
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    viewModel: FoodListViewModel = hiltViewModel()
) {


    val summaryFoods by viewModel.summaryFood.collectAsState()
    val savedSummaryFoods = rememberSaveable { mutableStateOf(summaryFoods) }

    LaunchedEffect(summaryFoods) {
        savedSummaryFoods.value = summaryFoods
    }

    var currentDt: String = "00000000"

    val inputDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    val outputDateFormat = DateTimeFormatter.ofPattern("yy.MM.dd")

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
//            BasicTextField(
//                value = searchQuery,
//                onValueChange = {  },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(42.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Done
//                ),
//                keyboardActions = KeyboardActions(
//                    onDone = {}
//                ),
//                maxLines = 1,
//                decorationBox = @Composable { innerTextField ->
//                    Box(
//                        modifier = Modifier
//                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
//                            .fillMaxWidth()
//                            .height(48.dp)
//                            .padding(horizontal = 16.dp)  // 전체 여백 추가
//                    ) {
//                        // 검색 아이콘
//                        Icon(
//                            imageVector = Icons.Default.Search,
//                            contentDescription = "Search Icon",
//                            modifier = Modifier
//                                .align(Alignment.CenterStart)  // 아이콘을 왼쪽에 정렬
//                                .padding(end = 8.dp)  // 아이콘과 텍스트 사이 여백
//                        )
//
//                        // 텍스트 필드를 가운데 정렬
//                        Box(
//                            modifier = Modifier
//                                .align(Alignment.Center)  // 텍스트 필드를 가운데 정렬
//                                .fillMaxWidth()  // 텍스트가 차지할 수 있는 공간을 확장
//                                .padding(start = 32.dp)  // 아이콘과 텍스트 사이의 여백 조정
//                        ) {
//                            innerTextField()
//                        }
//                    }
//                }
//            )
        }

        items(items = savedSummaryFoods.value) { food ->
            if (currentDt != food.endDt) {
                val formattedEndDt =
                    LocalDate.parse(food.endDt, inputDateFormat).format(outputDateFormat)

                Text(
                    text = formattedEndDt,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Food(food)

            currentDt = food.endDt
        }
    }
}

@Composable
fun Food(
    summaryFood: SummaryFoodModel
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { Unit }
            .height(72.dp)
            .background(color = CustomBoxBackGroundColor, shape = RoundedCornerShape(4.dp))
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.test_food_image),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(40.dp)
                .align(Alignment.CenterVertically)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = summaryFood.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "구매일자: " + summaryFood.startDt,
                style = MaterialTheme.typography.bodySmall,
                color = CustomGrayTextColor
            )
        }

        // 현재 날짜
        val currentDate = LocalDate.now()

        // 종료 날짜
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val targetDateParsed = LocalDate.parse(summaryFood.endDt, formatter)

        // 날짜 차이 계산
        val period = Period.between(currentDate, targetDateParsed)

        Text(
            text = if (period.days < 0) "D+${-period.days}" else "D-${period.days}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = CustomPrimaryColor,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}
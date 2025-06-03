package com.yh.fridgesoksok.presentation.edit_food.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Composable
fun EditFoodDateInput(formatted: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F7F9))
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = formatted,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            modifier = Modifier.align(Alignment.CenterEnd),
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null
        )
    }
}


@Composable
fun EditFoodDateSelector(
    selectedDate: LocalDate?,
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }
    val maxDate = remember { today.plusYears(2) }
    val minMonth = remember { YearMonth.from(today) }
    val maxMonth = remember { YearMonth.from(maxDate) }

    val totalMonths = remember { minMonth.until(maxMonth, ChronoUnit.MONTHS).toInt() + 1 }
    val initialPage = remember {
        val selectedMonth = YearMonth.from(selectedDate ?: today.plusWeeks(2))
        val offset = minMonth.until(selectedMonth, ChronoUnit.MONTHS).toInt()
        offset.coerceIn(0, totalMonths - 1)
    }
    val pagerState = rememberPagerState(
        pageCount = { totalMonths },
        initialPage = initialPage
    )
    val coroutineScope = rememberCoroutineScope()
    var tempSelectedDate by remember { mutableStateOf(selectedDate ?: today.plusWeeks(2)) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val currentMonth = remember(pagerState.currentPage) {
                    minMonth.plusMonths(pagerState.currentPage.toLong())
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
                    }
                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy M월")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = {
                            if (pagerState.currentPage < totalMonths - 1) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage < totalMonths - 1
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.height(240.dp)
                ) { page ->
                    val month = minMonth.plusMonths(page.toLong())
                    val firstDay = month.atDay(1)
                    val daysInMonth = month.lengthOfMonth()
                    val firstDayOfWeek = firstDay.dayOfWeek.value % 7
                    val totalGrid = ceil((firstDayOfWeek + daysInMonth) / 7f).toInt() * 7

                    Column {
                        for (row in 0 until totalGrid / 7) {
                            Row(Modifier.fillMaxWidth()) {
                                for (col in 0..6) {
                                    val index = row * 7 + col
                                    val dayNum = index - firstDayOfWeek + 1
                                    val date = if (index >= firstDayOfWeek && dayNum <= daysInMonth) {
                                        month.atDay(dayNum)
                                    } else null

                                    val isDisabled = date == null || date.isBefore(today) || date.isAfter(maxDate)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (date) {
                                                    tempSelectedDate -> Color(0xFF0066FF)
                                                    today -> Color(0xFFE0E0E0)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable(enabled = !isDisabled && date != null) {
                                                date?.let { tempSelectedDate = it }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date?.dayOfMonth?.toString() ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = when {
                                                isDisabled -> Color.LightGray
                                                date == tempSelectedDate -> Color.White
                                                else -> Color.Black
                                            },
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = "취소",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    TextButton(onClick = {
                        onConfirm(tempSelectedDate)
                        onDismissRequest()
                    }) {
                        Text(
                            text = "확인",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
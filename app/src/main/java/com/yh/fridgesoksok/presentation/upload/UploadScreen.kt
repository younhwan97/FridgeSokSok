package com.yh.fridgesoksok.presentation.upload

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yh.fridgesoksok.presentation.theme.CustomBackGroundColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: UploadViewModel = hiltViewModel()
) {
    val newFoods by viewModel.newFoods.collectAsState()

    LaunchedEffect(Unit) {
        val capturedImage =
            navController.previousBackStackEntry?.savedStateHandle?.get<Bitmap>("capturedImage")

        if (capturedImage != null) { viewModel.uploadReceiptImage(capturedImage)
        } else { viewModel.insertEmptyFood() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                title = {
                    Text(
                        text = "식품 추가하기",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight(500
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.insertEmptyFood() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "추가하기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(innerPadding)
                .background(CustomBackGroundColor)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(newFoods, key = { _, food -> food.id }) { index, food ->
                FoodInputBlock(
                    index = index,
                    name = food.name,
                    type = food.type,
                    count = food.count,
                    endDt = food.endDt,
                    onNameChange = { viewModel.updateFoodName(index, it) },
                    onTypeChange = { viewModel.updateFoodType(index, it) },
                    onCountChange = { viewModel.updateFoodCount(index, it) },
                    onDeleteClick = { viewModel.deleteFood(index) },
                    onDateChange = { viewModel.updateFoodEndDate(index, it) },
                )
            }
        }
    }
}

@Composable
fun FoodInputBlock(
    index: Int,
    name: String,
    type: Int,
    count: Int,
    endDt: String,
    onNameChange: (String) -> Unit,
    onTypeChange: (Int) -> Unit,
    onCountChange: (Int) -> Unit,
    onDateChange: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            //.background(color = CustomSurfaceColor, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "식품 ${index + 1}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight(600)
            )

            Box(
                modifier = Modifier
                    .padding(0.dp)
                    .clickable(onClick = onDeleteClick)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "삭제",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FoodNameInput(name, onNameChange)

        FoodCountInput(count, onCountChange)

        FoodTypeSelector(type, onTypeChange)

        FoodDateInputTrigger(endDt, onDateChange)
    }
}

@Composable
fun FoodNameInput(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "이름",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            fontWeight = FontWeight(500),
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            if (name.isEmpty()) {
                Text(
                    text = "식품 이름을 입력하세요",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
            )
        }
    }
}


@Composable
fun FoodTypeSelector(
    selectedType: Int,
    onTypeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryOptions = listOf("반찬", "음료", "과일", "육류", "냉동", "소스", "유제품", "채소", "과자", "기타")
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = categoryOptions.getOrNull(selectedType) ?: ""

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "유형",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            fontWeight = FontWeight(500),
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = modifier
                .fillMaxWidth(0.5f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(6.dp)
                    .clickable { expanded = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = selectedLabel.ifEmpty { "유형" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f) // 왼쪽 정렬
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "열기",
                        tint = Color.Gray,
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .heightIn(max = 300.dp)
            ) {
                categoryOptions.forEachIndexed { index, category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onTypeSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FoodCountInput(
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(count.toString()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "수량",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            fontWeight = FontWeight(500),
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = modifier
                .width(48.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        newValue.toIntOrNull()?.let {
                            if (it <= 99) {
                                text = newValue
                                onCountChange(it)
                            }
                        } ?: run {
                            text = newValue
                        }
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }
}

@Composable
fun FoodDateInputTrigger(
    date: String,
    onDateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    // 현재 날짜 or 기존 선택된 날짜
    val initialDate = date.takeIf { it.isNotEmpty() }?.let {
        LocalDate.parse(it, formatter)
    } ?: LocalDate.now()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "소비기한",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            fontWeight = FontWeight(500),
            modifier = Modifier.width(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(8.dp)
                .clickable {
                    val picker = android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selected = LocalDate.of(year, month + 1, dayOfMonth)
                            onDateChange(selected.format(formatter))
                        },
                        initialDate.year,
                        initialDate.monthValue - 1,
                        initialDate.dayOfMonth
                    )
                    picker.datePicker.minDate = System.currentTimeMillis()
                    picker.show()
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (date.isEmpty()) "소비기한(년-월-일)" else date,
                    color = if (date.isEmpty()) Color.Gray else Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "날짜 선택",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StaticMonthCalendar(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = remember(currentMonth) {
        val firstDay = currentMonth.atDay(1)
        val lastDay = currentMonth.atEndOfMonth()
        val days = mutableListOf<LocalDate>()

        val firstDayOfWeek = firstDay.dayOfWeek.value % 7
        repeat(firstDayOfWeek) { days.add(LocalDate.MIN) }

        var date = firstDay
        while (!date.isAfter(lastDay)) {
            days.add(date)
            date = date.plusDays(1)
        }

        val remaining = 7 - (days.size % 7)
        if (remaining < 7) repeat(remaining) { days.add(LocalDate.MIN) }

        days
    }

    Card(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "이전 달", modifier = Modifier.clickable {
                    currentMonth = currentMonth.minusMonths(1)
                })
                Text(
                    text = "${currentMonth.year}년 ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(Icons.Default.ArrowForward, contentDescription = "다음 달", modifier = Modifier.clickable {
                    currentMonth = currentMonth.plusMonths(1)
                })
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 요일 헤더
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 날짜
            daysInMonth.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    week.forEach { date ->
                        if (date == LocalDate.MIN) {
                            Spacer(modifier = Modifier.weight(1f).size(36.dp))
                        } else {
                            val isSelected = date == selectedDate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { onDateSelected(date) }
                                    .size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
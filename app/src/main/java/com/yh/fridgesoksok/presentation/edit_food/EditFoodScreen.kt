package com.yh.fridgesoksok.presentation.edit_food

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor4
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Composable
fun EditFoodScreen(
    navController: NavController
) {
    var selectedType by remember { mutableStateOf(Type.Ingredients) }
    var foodName by remember { mutableStateOf("") }
    var count by remember { mutableIntStateOf(1) }
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(today.plusWeeks(2)) }
    var showDialog by remember { mutableStateOf(false) }
    val formatted = selectedDate?.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) ?: "9999.99.99"
    var isClicking by remember { mutableStateOf(false) }

    val newFood = remember(selectedType, foodName, count, selectedDate) {
        FoodModel(
            id = "",
            fridgeId = "",
            itemName = foodName,
            expiryDate = selectedDate?.format(DateTimeFormatter.ofPattern("yyyyMMdd")) ?: "99999999",
            categoryId = selectedType.id,
            count = count,
            createdAt = ""
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditFoodTopAppBar(onNavigationClick = { navController.popBackStack() })
        },
        bottomBar = {
            EditFoodBottomButton(onClick = {
                if (!isClicking) {
                    isClicking = true
                    navController.previousBackStackEntry?.savedStateHandle?.set("newFood", newFood)
                    navController.popBackStack()
                }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            EditFoodImage(selectedType.iconLarge)
            Spacer(modifier = Modifier.height(20.dp))
            EditFoodLabeledField("유형") { EditFoodTypeDropDown(selectedType) { selectedType = it } }
            EditFoodLabeledField("식품명") { EditFoodName(foodName) { foodName = it } }
            EditFoodLabeledField("수량") { EditFoodCount(count) { count = it } }
            EditFoodLabeledField("소비기한") { EditFoodDateInput(formatted, selectedDate != null) { showDialog = true } }
        }

        if (showDialog) {
            EditFoodDateSelector(
                selectedDate = selectedDate,
                onDismissRequest = { showDialog = false },
                onConfirm = { selectedDate = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodTopAppBar(
    onNavigationClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            Image(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigationClick() },
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        },
        title = {
            Text(
                text = "식품 추가하기",
                style = MaterialTheme.typography.headlineMedium,
                color = CustomGreyColor7
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun EditFoodImage(
    iconRes: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            contentScale = ContentScale.None
        )
    }
}

@Composable
fun EditFoodLabeledField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = CustomGreyColor7
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodTypeDropDown(
    selectedType: Type,
    onTypeSelected: (Type) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedType.label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .heightIn(max = 240.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val visibleTypes = Type.entries.drop(1)

            visibleTypes.forEachIndexed() { index, type ->
                DropdownMenuItem(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    text = {
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )

                if (index < visibleTypes.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        }
    }
}

@Composable
fun EditFoodName(
    foodName: String,
    onNameChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        value = foodName,
        onValueChange = { onNameChanged(it) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
        }),
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (foodName.isEmpty()) {
                    Text(
                        text = "식품 이름을 적어주세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = CustomGreyColor4,
                        textAlign = TextAlign.Center
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun EditFoodCount(
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { if (count > 1) onCountChange(count - 1) },
            painter = painterResource(R.drawable.minus_primary),
            contentDescription = null,
            contentScale = ContentScale.None
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = CustomGreyColor7
        )
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { onCountChange(count + 1) },
            painter = painterResource(R.drawable.plus_primary),
            contentDescription = null,
            contentScale = ContentScale.None
        )
    }
}

@Composable
fun EditFoodDateInput(formatted: String, isValid: Boolean, onClick: () -> Unit) {
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
            color = if (isValid) Color.Black else Color(0xFFB4B4C0),
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

@Composable
fun EditFoodBottomButton(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 16.dp)
            .offset(y = (-12).dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Button(
            onClick = { onClick() },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "추가하기",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = CustomGreyColor1
            )
        }
    }
}
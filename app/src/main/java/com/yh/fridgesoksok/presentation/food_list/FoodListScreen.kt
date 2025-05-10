package com.yh.fridgesoksok.presentation.food_list

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor3
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor4
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    modifier: Modifier = Modifier,
    viewModel: FoodListViewModel = hiltViewModel()
) {
    val foods by viewModel.foodList.collectAsState()
    var input by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(Type.All) }

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (input.isBlank()) {
        searchQuery = ""
    }

    Column(modifier = modifier) {
        SearchBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = input,
            onValueChange = { if (it.length < 30) input = it },
            onDone = {
                searchQuery = input
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TypeChips(
            types = Type.entries,
            selectedType = selectedType,
            onTypeSelected = { selectedType = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FoodListContent(
            modifier = Modifier.fillMaxSize(),
            foods = foods,
            searchQuery = searchQuery,
            selectedType = selectedType,
            scrollState = scrollState
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = { inner ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.padding(start = 16.dp, end = 10.dp),
                    painter = painterResource(R.drawable.search),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = "검색할 내용을 입력해주세요!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                            color = CustomGreyColor4
                        )
                    }
                    inner()
                }
            }
        }
    )
}

@Composable
fun TypeChips(
    types: List<Type>,
    selectedType: Type,
    onTypeSelected: (Type) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(types) { index, type ->
            if (index == 0)
                Spacer(modifier = Modifier.width(16.dp))

            val isSelected = type == selectedType
            FilterChip(
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(24.dp),
                label = { Text(type.label) },
                selected = isSelected,
                onClick = { onTypeSelected(type) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
            )

            if (index == types.size - 1)
                Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun FoodListContent(
    modifier: Modifier = Modifier,
    foods: List<FoodModel>,
    searchQuery: String,
    selectedType: Type,
    scrollState: ScrollState
) {
    Box(
        modifier = modifier
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
                    .verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    foods
                        .filter { it.name.contains(searchQuery, ignoreCase = true) }
                        .filter { selectedType == Type.All || it.type == selectedType.id }
                        .chunked(2)
                        .forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                rowItems.forEach { food ->
                                    FoodCard(
                                        food = food,
                                        period = Period.between(
                                            LocalDate.now(),
                                            LocalDate.parse(
                                                food.endDt,
                                                DateTimeFormatter.ofPattern("yyyyMMdd")
                                            )
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // 짝수 개가 아닐 때 빈 칸 채우기
                                if (rowItems.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                }

                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    painter = painterResource(id = R.drawable.lighting),
                    contentScale = ContentScale.None,
                    contentDescription = null,
                )
            }
        }
    }
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
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(12.dp)
    ) {
        // D-Day 태그
        Box(
            modifier = Modifier
                .background(
                    if (dDay < 0) MaterialTheme.colorScheme.error else CustomGreyColor3,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = dDayText, color = Color.White, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 아이콘
        val iconRes = Type.entries.firstOrNull { it.id == food.type }?.icon ?: R.drawable.health
        Image(
            painter = painterResource(iconRes),
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
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

enum class Type(val id: Int, val label: String, @DrawableRes val icon: Int) {
    All(0, "전체", R.drawable.ingredients),
    Ingredients(1, "식재료", R.drawable.ingredients),
    Beverages(2, "음료", R.drawable.beverages),
    Bakery(3, "빵", R.drawable.bakery),
    Canned(4, "캔 & 병", R.drawable.canned),
    Sauces(5, "소스", R.drawable.sauces),
    Dairy(6, "유제품", R.drawable.daily),
    Frozen(7, "냉동", R.drawable.frozen),
    Health(8, "건강식품", R.drawable.health),
    Instant(9, "인스턴트", R.drawable.instant),
    Kimchi(10, "김치", R.drawable.kimchi),
    Meat(11, "고기", R.drawable.meat),
    Noodle(12, "면 & 파스타", R.drawable.noodle),
    SideDish(13, "반찬", R.drawable.side),
    Seafood(14, "해산물", R.drawable.seafood),
    Snack(15, "과자", R.drawable.snack),
    Tofu(16, "견과류", R.drawable.tofu),
}

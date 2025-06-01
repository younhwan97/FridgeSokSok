package com.yh.fridgesoksok.presentation.fridge.comp

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.fridge.FridgeState
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.model.Type
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
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.outline)
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                modifier = Modifier.align(Alignment.TopCenter),
                painter = painterResource(id = R.drawable.lighting),
                contentScale = ContentScale.None,
                contentDescription = null,
            )

            Crossfade(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp, start = 16.dp, end = 16.dp),
                targetState = fridgeState
            ) { state ->
                when (state) {
                    is FridgeState.Success -> FoodListContent(
                        foods = foods,
                        searchQuery = searchQuery,
                        selectedType = selectedType,
                        onClickCard = onClickCard,
                        onClickMinus = onClickMinus,
                        onClickPlus = onClickPlus
                    )

                    else -> Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodListContent(
    foods: List<FoodModel>,
    searchQuery: String,
    selectedType: Type,
    onClickCard: (FoodModel) -> Unit,
    onClickMinus: (FoodModel) -> Unit,
    onClickPlus: (FoodModel) -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val filteredFoods = foods
        .filter { it.itemName.contains(searchQuery, ignoreCase = true) }
        .filter { selectedType == Type.All || it.categoryId == selectedType.id }
        .chunked(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        items(
            items = filteredFoods,
            key = { row -> row.first().id + row.first().fridgeId }
        ) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { food ->
                    val period = Period.between(LocalDate.now(), LocalDate.parse(food.expiryDate, dateFormatter))
                    FoodListItem(
                        modifier = Modifier.weight(1f),
                        food = food,
                        period = period,
                        onClick = { onClickCard(food) },
                        onClickMinus = onClickMinus,
                        onClickPlus = onClickPlus,
                    )
                }

                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
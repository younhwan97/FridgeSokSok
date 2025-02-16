package com.yh.fridgesoksok.ui.list

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.domain.model.SummaryFood
import com.yh.fridgesoksok.presentation.list.FoodListViewModel
import com.yh.fridgesoksok.presentation.model.SummaryFoodModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    viewModel: FoodListViewModel = hiltViewModel()
) {
    var currentDt = ""
    LazyColumn{
        items(items = viewModel.summaryFood.value){ food ->
            if (currentDt != food.endDt){
                Text(food.endDt)
            }

            Food(food)

            currentDt = food.endDt
        }
    }
}

@Composable
fun Food(
    summaryFood: SummaryFoodModel
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Column {
            Text(summaryFood.name)
            Spacer(modifier = Modifier.height(4.dp))
            Text(summaryFood.startDt)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
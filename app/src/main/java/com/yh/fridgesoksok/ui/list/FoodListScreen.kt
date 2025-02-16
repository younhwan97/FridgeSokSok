package com.yh.fridgesoksok.ui.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.presentation.list.FoodListViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FoodListScreen(
    viewModel: FoodListViewModel = hiltViewModel()
) {
    Text("hello")

    Log.v("123", viewModel.summaryFood.value.toString())
}
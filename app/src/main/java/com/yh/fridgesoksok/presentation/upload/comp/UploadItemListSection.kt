package com.yh.fridgesoksok.presentation.upload.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.comp.BlockingLoadingOverlay
import com.yh.fridgesoksok.presentation.model.FoodModel
import com.yh.fridgesoksok.presentation.upload.UploadState

@Composable
fun UploadItemListSection(
    uploadState: UploadState,
    innerPadding: PaddingValues,
    foods: List<FoodModel>,
    onEdit: (FoodModel) -> Unit,
    onDelete: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit
) {
    val listState = rememberLazyListState()

    when (uploadState) {
        UploadState.Success, UploadState.Uploading -> {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                state = listState
            ) {
                items(foods, key = { it.id }) { food ->
                    UploadItem(
                        food = food,
                        onEdit = { onEdit(food) },
                        onDelete = { onDelete(food.id) },
                        onIncrease = { onIncrease(food.id) },
                        onDecrease = { onDecrease(food.id) }
                    )
                }
            }
        }

        UploadState.Loading -> BlockingLoadingOverlay()
        UploadState.Error -> Unit
    }
}
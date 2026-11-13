package com.yh.fridgesoksok.presentation.upload.comp

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.model.FoodModel

@Composable
fun UploadItemListSection(
    innerPadding: PaddingValues,
    foods: List<FoodModel>,
    receiptPreview: Bitmap? = null,
    onReceiptClick: () -> Unit = {},
    onEdit: (FoodModel) -> Unit,
    onDelete: (String) -> Unit,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
        state = listState
    ) {
        // 원본 영수증 (인식 결과와 비교)
        if (receiptPreview != null && foods.isNotEmpty()) {
            item(key = "receipt_preview") {
                UploadReceiptPreviewCard(receipt = receiptPreview, onClick = onReceiptClick)
            }
        }

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
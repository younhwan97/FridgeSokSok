package com.yh.fridgesoksok.presentation.fridge.comp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.model.Type

@Composable
fun FoodTypeChips(
    types: List<Type>,
    selectedType: Type,
    onTypeSelected: (Type) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(types) { index, type ->
            if (index == 0) Spacer(modifier = Modifier.width(16.dp))

            FilterChip(
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(24.dp),
                label = { Text(type.label) },
                selected = type == selectedType,
                onClick = { onTypeSelected(type) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (type == selectedType) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            )

            if (index == types.size - 1) Spacer(modifier = Modifier.width(16.dp))
        }
    }
}
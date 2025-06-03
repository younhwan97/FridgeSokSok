package com.yh.fridgesoksok.presentation.edit_food.comp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.yh.fridgesoksok.presentation.model.LocalFoodModel
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7
import java.time.format.DateTimeFormatter

@Composable
fun EditFoodNameSuggestion(
    suggestions: List<LocalFoodModel>,
    suggestionOffsetY: Float,
    onSuggestionSelected: (LocalFoodModel) -> Unit,
    formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = with(density) { suggestionOffsetY.toDp().roundToPx() }
                )
            }
            .padding(horizontal = 16.dp)
            .zIndex(999f)
            .heightIn(max = 180.dp),
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 8.dp,
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn {
            items(suggestions) { suggestion ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable {
                            onSuggestionSelected(suggestion)
                            focusManager.clearFocus()
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = suggestion.itemName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CustomGreyColor7
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFE0E0E0)
                )
            }
        }
    }
}
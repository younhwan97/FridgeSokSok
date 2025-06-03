package com.yh.fridgesoksok.presentation.edit_food.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.model.Type

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodType(
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
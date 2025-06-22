package com.yh.fridgesoksok.presentation.recipe.comp

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.common.comp.SearchBar

@Composable
fun RecipeSearchSection(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchConfirmed: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    SearchBar(
        modifier = Modifier.padding(horizontal = 16.dp),
        value = value,
        onValueChange = {
            if (it.length <= 30) onValueChange(it)
        },
        onDone = {
            onSearchConfirmed()
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    )
}

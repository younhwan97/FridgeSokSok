package com.yh.fridgesoksok.presentation.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yh.fridgesoksok.presentation.common.SearchBar

@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scrollState = rememberScrollState()
    var input by remember { mutableStateOf("") }

    // Content
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        RecipeSearchSection(
            value = input,
            onValueChange = { input = it },
            onSearchConfirmed = {
                viewModel.updateSearchQuery(input)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            recipes
                .filter { it.recipeName.contains(searchQuery, ignoreCase = true) || it.recipeContent.contains(searchQuery, ignoreCase = true) }
                .forEach { item ->
                    Column {
                        Text(
                            text = item.recipeName
                        )
                        Spacer(modifier = Modifier.height(60.dp).background(color = Color.Black))
                    }
                }
        }
    }
}

@Composable
private fun RecipeSearchSection(
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

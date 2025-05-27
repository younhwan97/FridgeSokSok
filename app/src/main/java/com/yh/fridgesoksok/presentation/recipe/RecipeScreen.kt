package com.yh.fridgesoksok.presentation.recipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecipeScreen(
    viewModel: RecipeViewModel = hiltViewModel()
) {

    // Content
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}
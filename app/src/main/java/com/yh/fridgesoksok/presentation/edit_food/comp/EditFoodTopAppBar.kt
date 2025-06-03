package com.yh.fridgesoksok.presentation.edit_food.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodTopAppBar(
    title: String,
    onNavigationClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            Image(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigationClick() },
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                contentScale = ContentScale.None
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = CustomGreyColor7
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
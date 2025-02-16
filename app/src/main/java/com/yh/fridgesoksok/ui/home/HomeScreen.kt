package com.yh.fridgesoksok.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.domain.model.Food
import com.yh.fridgesoksok.ui.list.FoodListScreen
import com.yh.fridgesoksok.ui.theme.CustomBackGroundColor
import com.yh.fridgesoksok.ui.theme.CustomPrimaryColor
import com.yh.fridgesoksok.ui.theme.CustomTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = CustomBackGroundColor,
                    titleContentColor = CustomTextColor,
                ),
                title = {
                    Text(
                        text = "Fridge",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Bottom app bar",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = CustomBackGroundColor)
                .fillMaxWidth()
                .fillMaxHeight()
            ,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FoodListScreen()
        }
    }
}
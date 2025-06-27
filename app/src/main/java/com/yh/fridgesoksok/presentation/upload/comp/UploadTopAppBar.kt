package com.yh.fridgesoksok.presentation.upload.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadTopAppBar(
    isBackEnabled: Boolean,
    onNavigationClick: () -> Unit,
    onActionClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = isBackEnabled) {
                        onNavigationClick()
                    },
                painter = painterResource(R.drawable.back),
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = "식품 추가하기",
                style = MaterialTheme.typography.headlineMedium,
                color = CustomGreyColor7
            )
        },
        actions = {
            Button(
                modifier = Modifier.padding(end = 16.dp),
                onClick = { onActionClick() },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "식품 추가",
                    style = MaterialTheme.typography.bodySmall,
                    color = CustomGreyColor1
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}
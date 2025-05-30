package com.yh.fridgesoksok.presentation.login.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R

@Composable
fun LoginAppLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo02),
            contentDescription = null,
            contentScale = ContentScale.None
        )
    }
}
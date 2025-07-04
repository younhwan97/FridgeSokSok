package com.yh.fridgesoksok.presentation.upload.comp

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor1
import com.yh.fridgesoksok.presentation.upload.UploadState

@Composable
fun UploadBottomButton(
    uploadState: UploadState,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 16.dp)
            .offset(y = (-12).dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Button(
            onClick = { onClick() },
            enabled = enabled,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uploadState == UploadState.Uploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = "추가하기",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = CustomGreyColor1
                )
            }
        }
    }
}
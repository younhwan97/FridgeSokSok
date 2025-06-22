package com.yh.fridgesoksok.presentation.common.comp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor2
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun ConfirmDialog(
    title: String = "알림",
    message: String,
    onConfirm: () -> Unit,
    confirmText: String = "확인",
    confirmTextColor: Color = MaterialTheme.colorScheme.primary,
    confirmContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onDismiss: () -> Unit,
    dismissText: String = "취소",
    dismissTextColor: Color = CustomGreyColor7,
    dismissContainerColor: Color = CustomGreyColor2
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 6.dp,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = confirmContainerColor)
            ) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = confirmTextColor
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = dismissContainerColor)
            ) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = dismissTextColor
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = CustomGreyColor7
            )
        }
    )
}
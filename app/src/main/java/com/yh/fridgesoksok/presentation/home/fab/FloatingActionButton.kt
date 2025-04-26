package com.yh.fridgesoksok.presentation.home.fab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.presentation.theme.CustomBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor

@Composable
fun FloatingActionButton(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val backgroundColor = if (expanded) CustomBackGroundColor else MaterialTheme.colorScheme.primary
    val icon = if (expanded) Icons.Default.Close else Icons.Default.Add
    val iconTint = if (expanded) Color.Black else MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        shape = CircleShape,
        color = backgroundColor,
        tonalElevation = 6.dp,
        onClick = {
            onClick()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                tint = iconTint,
                contentDescription = null
            )

            if (!expanded)
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "추가",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight(500),
                )
        }
    }
}
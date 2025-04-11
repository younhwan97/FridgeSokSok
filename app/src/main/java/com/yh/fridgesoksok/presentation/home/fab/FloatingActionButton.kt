package com.yh.fridgesoksok.presentation.home.fab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.zIndex
import com.yh.fridgesoksok.presentation.theme.CustomBackGroundColor
import com.yh.fridgesoksok.presentation.theme.CustomPrimaryColor

@Composable
fun FloatingActionButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        onClick = {
            onClick()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        },
        shape = CircleShape,
        color = if (!expanded) CustomPrimaryColor else CustomBackGroundColor,
        tonalElevation = 6.dp,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .zIndex(2f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                tint = if (!expanded) MaterialTheme.colorScheme.onPrimary else Color.Black,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            if (!expanded) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "추가하기",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

//        // FAB(Floating Action Button)
//        ExtendedFloatingActionButton(
//            onClick = {
//                isFabMenuExpanded = !isFabMenuExpanded
//                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
//            },
//            shape = CircleShape,
//            modifier = Modifier
//                .zIndex(2f)
//                .onGloballyPositioned { coordinates ->
//                    fabOffset = coordinates.positionInRoot()
//                }
//                .align(Alignment.BottomEnd)
//                .windowInsetsPadding(WindowInsets.navigationBars)
//                .padding(end = 16.dp, bottom = 16.dp)
//        ) {
//            Icon(
//                imageVector = if (isFabMenuExpanded) Icons.Default.Close else Icons.Default.Add,
//                contentDescription = null,
//            )
//
//            if (!isFabMenuExpanded) {
//                Text(
//                    text = "추가하기",
//                    style = MaterialTheme.typography.bodyMedium,
//                )
//            }
//        }
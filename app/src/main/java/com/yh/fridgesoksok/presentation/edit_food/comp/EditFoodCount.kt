package com.yh.fridgesoksok.presentation.edit_food.comp

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yh.fridgesoksok.R
import com.yh.fridgesoksok.presentation.theme.CustomGreyColor7

@Composable
fun EditFoodCount(
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { if (count > 1) onCountChange(count - 1) },
            painter = painterResource(R.drawable.minus_primary),
            contentDescription = null,
            contentScale = ContentScale.None
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = CustomGreyColor7
        )
        Image(
            modifier = Modifier
                .size(24.dp)
                .clickable { onCountChange(count + 1) },
            painter = painterResource(R.drawable.plus_primary),
            contentDescription = null,
            contentScale = ContentScale.None
        )
    }
}

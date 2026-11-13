package com.chainservice.log.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chainservice.log.core.theme.TextSecondary

@Composable
fun EmptyState(
    spriteRes: Int,
    title: String,
    modifier: Modifier = Modifier,
    spriteSize: Dp = 72.dp,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    MetalPlate(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(spriteRes),
                contentDescription = null,
                modifier = Modifier.size(spriteSize),
                contentScale = ContentScale.Fit
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            if (actionText != null && onAction != null) {
                SecondaryButton(text = actionText, onClick = onAction)
            }
        }
    }
}

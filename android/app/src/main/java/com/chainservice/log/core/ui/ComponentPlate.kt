package com.chainservice.log.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chainservice.log.R
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.stateColor

@Composable
fun ComponentPlate(
    component: ComponentUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MetalPlate(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 104.dp)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Labels.sprite(component.type)),
                contentDescription = stringResource(
                    R.string.cd_component_sprite,
                    component.name
                ),
                modifier = Modifier.size(44.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = component.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StateChip(state = component.state)
                }
                WearLinkBar(
                    ratio = component.ratio,
                    state = component.state,
                    description = stringResource(
                        R.string.wear_state_description,
                        component.percent
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TabularNumber(
                        value = stringResource(
                            R.string.wear_progress,
                            component.wornText,
                            component.limitText,
                            stringResource(Labels.unit(component.unit))
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.wear_left,
                            component.remainingText,
                            stringResource(Labels.unit(component.unit))
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = stateColor(component.state),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

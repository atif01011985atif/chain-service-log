package com.chainservice.log.presentation.menu

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.SignalOrangePulse
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.BikePlate
import com.chainservice.log.core.ui.EmptyState
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.PrimaryButton
import com.chainservice.log.core.ui.SecondaryButton
import com.chainservice.log.core.ui.SectionLabel
import com.chainservice.log.core.ui.StatCard
import com.chainservice.log.core.ui.TabularNumber
import com.chainservice.log.presentation.dialog.AddBikeDialog

@Composable
fun MenuScreen(
    onOpenBike: (String) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = ServiceLocator.factory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    var showAddBike by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        menuViewModel.openBike.collect { bikeId -> onOpenBike(bikeId) }
    }

    if (showAddBike) {
        AddBikeDialog(
            unitLabel = stringResource(Labels.unit(state.unit)),
            onDismiss = { showAddBike = false },
            onConfirm = { name, frame, odometer ->
                showAddBike = false
                menuViewModel.addBike(name, frame, odometer)
            }
        )
    }

    PegboardBackground(
        overlay = listOf(
            Color(0xCC1A1D21),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = SignalOrange,
        blobAlignment = Alignment.TopEnd,
        dotAlpha = 0.4f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {
            MenuHeader(totalText = state.totalDistanceText, unitLabel = Labels.unit(state.unit))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.menu_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PrimaryButton(
                    text = stringResource(R.string.start_logging),
                    onClick = {
                        if (state.isEmpty) showAddBike = true else menuViewModel.startLogging()
                    },
                    icon = Icons.Filled.Build,
                    height = 96.dp,
                    modifier = Modifier.weight(1.6f)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CountTile(
                        label = stringResource(R.string.label_bikes),
                        value = state.bikeCount.toString(),
                        accent = TextPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    DueTile(
                        count = state.dueCount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            if (!state.isEmpty) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        label = stringResource(R.string.label_distance_logged),
                        value = state.totalDistanceText,
                        accent = SignalOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.label_services),
                        value = state.serviceCount.toString(),
                        accent = Lime,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.hasSpent) {
                        StatCard(
                            label = stringResource(R.string.label_spent),
                            value = state.spentText,
                            accent = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionLabel(text = stringResource(R.string.section_your_bikes))

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isEmpty) {
                Box(modifier = Modifier.weight(1f)) {
                    EmptyState(
                        spriteRes = R.drawable.sprite_5,
                        title = stringResource(R.string.empty_rack),
                        spriteSize = 56.dp,
                        actionText = stringResource(R.string.add_bike),
                        onAction = { showAddBike = true }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = state.bikes, key = { it.id }) { bike ->
                        BikePlate(bike = bike, onClick = { onOpenBike(bike.id) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = stringResource(R.string.add_bike),
                onClick = { showAddBike = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                UtilityTile(
                    label = stringResource(R.string.history),
                    icon = Icons.Filled.DateRange,
                    onClick = onOpenHistory,
                    modifier = Modifier.weight(1f)
                )
                UtilityTile(
                    label = stringResource(R.string.settings),
                    icon = Icons.Filled.Settings,
                    onClick = onOpenSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MenuHeader(totalText: String, unitLabel: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.icon_home),
            contentDescription = stringResource(R.string.cd_rack_icon),
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.menu_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        MetalPlate(modifier = Modifier.width(132.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Lime, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TabularNumber(
                    value = totalText,
                    style = MaterialTheme.typography.labelLarge,
                    color = Lime
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(unitLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun CountTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    MetalPlate(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TabularNumber(
                value = value,
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun DueTile(count: Int, modifier: Modifier = Modifier) {
    val tint by animateColorAsState(
        targetValue = if (count > 0) SignalOrangePulse else TextSecondary,
        label = "dueTint"
    )
    MetalPlate(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
            verticalArrangement = Arrangement.Center
        ) {
            TabularNumber(
                value = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = tint
            )
            Text(
                text = stringResource(R.string.label_due),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun UtilityTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MetalPlate(modifier = modifier, onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Lime,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
    }
}

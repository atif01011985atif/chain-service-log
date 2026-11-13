package com.chainservice.log.presentation.history

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chainservice.log.R
import com.chainservice.log.core.di.ServiceLocator
import com.chainservice.log.core.theme.Lime
import com.chainservice.log.core.theme.SignalOrange
import com.chainservice.log.core.theme.TextPrimary
import com.chainservice.log.core.theme.TextSecondary
import com.chainservice.log.core.theme.TextTertiary
import com.chainservice.log.core.ui.ChoicePill
import com.chainservice.log.core.ui.EmptyState
import com.chainservice.log.core.ui.Labels
import com.chainservice.log.core.ui.MetalPlate
import com.chainservice.log.core.ui.PegboardBackground
import com.chainservice.log.core.ui.ScreenHeader
import com.chainservice.log.core.ui.SectionLabel
import com.chainservice.log.core.ui.TabularNumber
import com.chainservice.log.domain.model.ComponentType

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val historyViewModel: HistoryViewModel = viewModel(factory = ServiceLocator.factory())
    val state by historyViewModel.state.collectAsStateWithLifecycle()

    PegboardBackground(
        overlay = listOf(
            Color(0xD91A1D21),
            Color(0xE61A1D21),
            Color(0xF21A1D21)
        ),
        blobColor = SignalOrange,
        blobAlignment = Alignment.BottomStart,
        dotAlpha = 0.35f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeaderWithCount(
                title = stringResource(R.string.history_title),
                count = state.totalCount,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ChoicePill(
                        text = stringResource(R.string.filter_all),
                        selected = state.filter.bikeId.isEmpty() && state.filter.type == null,
                        onClick = {
                            historyViewModel.selectBike("")
                            historyViewModel.selectType(null)
                        }
                    )
                }
                items(items = state.bikes, key = { it.id }) { bike ->
                    ChoicePill(
                        text = bike.name,
                        selected = state.filter.bikeId == bike.id,
                        onClick = { historyViewModel.selectBike(bike.id) }
                    )
                }
                items(items = ComponentType.entries.toList(), key = { it.name }) { type ->
                    ChoicePill(
                        text = stringResource(Labels.type(type)),
                        selected = state.filter.type == type,
                        onClick = {
                            historyViewModel.selectType(
                                if (state.filter.type == type) null else type
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.isEmpty) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    EmptyState(
                        spriteRes = R.drawable.sprite_1,
                        title = stringResource(R.string.history_empty)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.sections.forEach { section ->
                        item(key = section.monthLabel) {
                            SectionLabel(text = section.monthLabel)
                        }
                        items(items = section.records, key = { it.id }) { record ->
                            HistoryRow(
                                dateText = record.dateText,
                                bikeName = record.bikeName,
                                componentName = record.componentName,
                                actionText = stringResource(record.actionLabel),
                                note = record.note,
                                odometerText = record.odometerText,
                                unitText = stringResource(Labels.unit(record.unit)),
                                costText = record.costText,
                                hasCost = record.hasCost,
                                spriteRes = Labels.sprite(record.componentType)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ScreenHeaderWithCount(title: String, count: Int, onBack: () -> Unit) {
    ScreenHeader(
        title = title,
        onBack = onBack,
        trailing = {
            MetalPlate(modifier = Modifier.width(72.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TabularNumber(
                        value = count.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Lime
                    )
                }
            }
        }
    )
}

@Composable
private fun HistoryRow(
    dateText: String,
    bikeName: String,
    componentName: String,
    actionText: String,
    note: String,
    odometerText: String,
    unitText: String,
    costText: String,
    hasCost: Boolean,
    spriteRes: Int
) {
    MetalPlate(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 84.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(84.dp)
                    .background(SignalOrange, RoundedCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                painter = painterResource(spriteRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Text(
                    text = stringResource(R.string.record_headline, componentName, actionText),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bikeName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                if (note.isNotEmpty()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.padding(end = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                TabularNumber(
                    value = odometerText,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Text(
                    text = unitText,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                if (hasCost) {
                    TabularNumber(
                        value = costText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Lime
                    )
                }
            }
        }
    }
}

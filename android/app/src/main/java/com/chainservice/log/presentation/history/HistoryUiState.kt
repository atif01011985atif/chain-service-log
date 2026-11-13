package com.chainservice.log.presentation.history

import com.chainservice.log.core.ui.RecordUi
import com.chainservice.log.domain.model.ComponentType

data class HistoryFilter(
    val bikeId: String = "",
    val type: ComponentType? = null
)

data class HistoryBike(
    val id: String,
    val name: String
)

data class HistorySection(
    val monthLabel: String,
    val records: List<RecordUi>
)

data class HistoryUiState(
    val sections: List<HistorySection> = emptyList(),
    val bikes: List<HistoryBike> = emptyList(),
    val filter: HistoryFilter = HistoryFilter(),
    val totalCount: Int = 0,
    val loaded: Boolean = false
) {
    val isEmpty: Boolean get() = sections.isEmpty()
}

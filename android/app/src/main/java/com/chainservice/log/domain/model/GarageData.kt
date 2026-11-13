package com.chainservice.log.domain.model

data class GarageData(
    val bikes: List<Bike> = emptyList(),
    val components: List<BikeComponent> = emptyList(),
    val records: List<ServiceRecord> = emptyList(),
    val spares: List<SparePart> = emptyList(),
    val loaded: Boolean = false
) {
    fun bike(id: String): Bike? = bikes.firstOrNull { it.id == id }

    fun componentsOf(bikeId: String): List<BikeComponent> =
        components.filter { it.bikeId == bikeId && !it.retired }

    fun component(id: String): BikeComponent? = components.firstOrNull { it.id == id }

    fun recordsOf(bikeId: String): List<ServiceRecord> =
        records.filter { it.bikeId == bikeId }.sortedByDescending { it.timestamp }
}

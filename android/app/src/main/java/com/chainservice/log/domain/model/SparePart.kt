package com.chainservice.log.domain.model

data class SparePart(
    val id: String,
    val name: String,
    val type: ComponentType,
    val count: Int
)

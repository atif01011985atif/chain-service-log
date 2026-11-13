package com.chainservice.log.domain.model

enum class ComponentType(val defaultLimitKm: Int) {
    CHAIN(4000),
    TYRE(5000),
    BRAKE(2000),
    CABLE(6000),
    OTHER(3000)
}

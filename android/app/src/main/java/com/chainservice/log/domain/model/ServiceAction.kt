package com.chainservice.log.domain.model

enum class ServiceAction(val resetsWear: Boolean) {
    LUBED(false),
    CLEANED(false),
    REPLACED(true),
    ADJUSTED(false),
    INSPECTED(false),
    RETIRED(true)
}

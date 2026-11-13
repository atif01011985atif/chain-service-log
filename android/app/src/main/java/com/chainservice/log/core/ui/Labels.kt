package com.chainservice.log.core.ui

import com.chainservice.log.R
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.DistanceUnit
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.model.ServiceAction
import com.chainservice.log.domain.model.WearState

object Labels {

    fun type(type: ComponentType): Int = when (type) {
        ComponentType.CHAIN -> R.string.type_chain
        ComponentType.TYRE -> R.string.type_tyre
        ComponentType.BRAKE -> R.string.type_brake
        ComponentType.CABLE -> R.string.type_cable
        ComponentType.OTHER -> R.string.type_other
    }

    fun frame(frame: FrameType): Int = when (frame) {
        FrameType.ROAD -> R.string.frame_road
        FrameType.GRAVEL -> R.string.frame_gravel
        FrameType.MTB -> R.string.frame_mtb
        FrameType.CITY -> R.string.frame_city
        FrameType.EBIKE -> R.string.frame_ebike
    }

    fun action(action: ServiceAction): Int = when (action) {
        ServiceAction.LUBED -> R.string.action_lubed
        ServiceAction.CLEANED -> R.string.action_cleaned
        ServiceAction.REPLACED -> R.string.action_replaced
        ServiceAction.ADJUSTED -> R.string.action_adjusted
        ServiceAction.INSPECTED -> R.string.action_inspected
        ServiceAction.RETIRED -> R.string.action_retired
    }

    fun unit(unit: DistanceUnit): Int = when (unit) {
        DistanceUnit.KM -> R.string.unit_km
        DistanceUnit.MI -> R.string.unit_mi
    }

    fun state(state: WearState): Int = when (state) {
        WearState.OK -> R.string.state_ok
        WearState.SOON -> R.string.state_soon
        WearState.DUE -> R.string.state_due
    }

    fun digit(value: Int): Int = when (value) {
        0 -> R.string.key_0
        1 -> R.string.key_1
        2 -> R.string.key_2
        3 -> R.string.key_3
        4 -> R.string.key_4
        5 -> R.string.key_5
        6 -> R.string.key_6
        7 -> R.string.key_7
        8 -> R.string.key_8
        else -> R.string.key_9
    }

    fun sprite(type: ComponentType): Int = when (type) {
        ComponentType.CHAIN -> R.drawable.sprite_1
        ComponentType.TYRE -> R.drawable.sprite_2
        ComponentType.BRAKE -> R.drawable.sprite_3
        ComponentType.CABLE -> R.drawable.sprite_4
        ComponentType.OTHER -> R.drawable.sprite_5
    }
}

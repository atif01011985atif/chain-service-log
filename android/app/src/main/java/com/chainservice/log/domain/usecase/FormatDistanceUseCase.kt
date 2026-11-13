package com.chainservice.log.domain.usecase

import com.chainservice.log.core.config.GameConfig
import com.chainservice.log.domain.model.DistanceUnit
import kotlin.math.roundToInt

class FormatDistanceUseCase {

    operator fun invoke(kilometres: Int, unit: DistanceUnit): String = group(convert(kilometres, unit))

    fun convert(kilometres: Int, unit: DistanceUnit): Int = when (unit) {
        DistanceUnit.KM -> kilometres
        DistanceUnit.MI -> (kilometres * GameConfig.MILES_PER_KM).roundToInt()
    }

    fun toKilometres(value: Int, unit: DistanceUnit): Int = when (unit) {
        DistanceUnit.KM -> value
        DistanceUnit.MI -> (value / GameConfig.MILES_PER_KM).roundToInt()
    }

    fun group(value: Int): String {
        val digits = value.toString()
        val builder = StringBuilder()
        digits.forEachIndexed { index, char ->
            if (index > 0 && (digits.length - index) % 3 == 0) {
                builder.append(' ')
            }
            builder.append(char)
        }
        return builder.toString()
    }
}

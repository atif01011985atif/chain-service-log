package com.chainservice.log.data.sample

import com.chainservice.log.domain.model.Bike
import com.chainservice.log.domain.model.BikeComponent
import com.chainservice.log.domain.model.ComponentType
import com.chainservice.log.domain.model.FrameType
import com.chainservice.log.domain.model.GarageData
import com.chainservice.log.domain.model.ServiceAction
import com.chainservice.log.domain.model.ServiceRecord
import com.chainservice.log.domain.model.SparePart
import java.util.Calendar
import java.util.GregorianCalendar

object SampleData {

    fun build(): GarageData {
        val commuter = Bike("bike-commuter", "COMMUTER", FrameType.CITY, 12480)
        val gravel = Bike("bike-gravel", "GRAVEL RIG", FrameType.GRAVEL, 4265)
        val road = Bike("bike-road", "WEEKEND ROAD", FrameType.ROAD, 2010)

        val components = listOf(
            BikeComponent("part-c1", commuter.id, "CHAIN", ComponentType.CHAIN, 9200, 4000, false),
            BikeComponent("part-c2", commuter.id, "REAR TYRE", ComponentType.TYRE, 8100, 5000, false),
            BikeComponent("part-c3", commuter.id, "BRAKE PADS", ComponentType.BRAKE, 11600, 2000, false),
            BikeComponent("part-c4", commuter.id, "SHIFT CABLES", ComponentType.CABLE, 7400, 6000, false),
            BikeComponent("part-g1", gravel.id, "CHAIN", ComponentType.CHAIN, 2900, 4000, false),
            BikeComponent("part-g2", gravel.id, "FRONT TYRE", ComponentType.TYRE, 1050, 5000, false),
            BikeComponent("part-r1", road.id, "CHAIN", ComponentType.CHAIN, 0, 4000, false)
        )

        val records = listOf(
            ServiceRecord(
                "log-1", commuter.id, "part-c1", ServiceAction.LUBED, 12480,
                "wet ride home, re-oiled", 0.0, date(2026, Calendar.NOVEMBER, 4)
            ),
            ServiceRecord(
                "log-2", commuter.id, "part-c3", ServiceAction.REPLACED, 11600,
                "squealing on descents", 18.0, date(2026, Calendar.OCTOBER, 21)
            ),
            ServiceRecord(
                "log-3", gravel.id, "part-g1", ServiceAction.CLEANED, 4120,
                "post-gravel degrease", 0.0, date(2026, Calendar.OCTOBER, 2)
            ),
            ServiceRecord(
                "log-4", commuter.id, "part-c2", ServiceAction.INSPECTED, 11240,
                "sidewall still fine", 0.0, date(2026, Calendar.SEPTEMBER, 14)
            ),
            ServiceRecord(
                "log-5", road.id, "part-r1", ServiceAction.LUBED, 1760,
                "dry wax, first coat", 0.0, date(2026, Calendar.AUGUST, 30)
            ),
            ServiceRecord(
                "log-6", gravel.id, "part-g2", ServiceAction.REPLACED, 1050,
                "cut from flint", 42.5, date(2026, Calendar.AUGUST, 11)
            ),
            ServiceRecord(
                "log-7", commuter.id, "part-c4", ServiceAction.ADJUSTED, 10880,
                "indexing drifted", 0.0, date(2026, Calendar.JULY, 27)
            )
        )

        val spares = listOf(
            SparePart("spare-1", "CHAIN 11-SPEED", ComponentType.CHAIN, 2),
            SparePart("spare-2", "BRAKE PADS", ComponentType.BRAKE, 3),
            SparePart("spare-3", "INNER TUBE 700C", ComponentType.TYRE, 4),
            SparePart("spare-4", "SHIFT CABLE", ComponentType.CABLE, 1)
        )

        return GarageData(
            bikes = listOf(commuter, gravel, road),
            components = components,
            records = records,
            spares = spares,
            loaded = true
        )
    }

    private fun date(year: Int, month: Int, day: Int): Long =
        GregorianCalendar(year, month, day, 9, 30).timeInMillis
}

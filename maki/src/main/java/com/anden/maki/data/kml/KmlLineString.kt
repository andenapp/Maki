package com.anden.maki.data.kml

import com.anden.maki.data.LineString
import com.google.android.gms.maps.model.LatLng

open class KmlLineString : LineString {

    val altitudes: List<Double>?

    constructor(coordinates: List<LatLng>) : this(coordinates, null)

    constructor(coordinates: List<LatLng>, altitudes: List<Double>?) : super(coordinates) {
        this.altitudes = altitudes
    }

    /**
     * Gets the coordinates
     *
     * @return ArrayList of LatLng
     */
    override fun getGeometryObject(): java.util.ArrayList<LatLng> {
        val coordinatesList = super.getGeometryObject()
        return ArrayList(coordinatesList)
    }
}
package com.anden.maki.data.kml

import com.anden.maki.data.Point
import com.google.android.gms.maps.model.LatLng

class KmlPoint : Point {

    val altitude: Double?

    constructor(coordinates: LatLng) : this(coordinates, null)

    constructor(coordinates: LatLng, altitude: Double?) : super(coordinates) {
        this.altitude = altitude
    }


}
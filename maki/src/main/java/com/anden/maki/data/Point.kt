package com.anden.maki.data

import com.google.android.gms.maps.model.LatLng

open class Point(val coordinates: LatLng) : Geometry<Any> {

    companion object {
        const val GEOMETRY_TYPE = "Point"
    }

    override fun getGeometryType() = GEOMETRY_TYPE

    override fun getGeometryObject() = coordinates

    override fun toString(): String {
        val sb = StringBuilder(GEOMETRY_TYPE).append("{")
        sb.append("\n coordinates=").append(coordinates)
        sb.append("\n}\n")
        return sb.toString()
    }
}
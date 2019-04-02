package com.anden.maki.data

import com.google.android.gms.maps.model.LatLng

open class LineString(val coordinates: List<LatLng>) : Geometry<List<LatLng>> {

    companion object {
        private const val GEOMETRY_TYPE = "LineString"
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
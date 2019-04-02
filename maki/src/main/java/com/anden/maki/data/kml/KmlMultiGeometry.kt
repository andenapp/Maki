package com.anden.maki.data.kml

import com.anden.maki.data.Geometry
import com.anden.maki.data.MultiGeometry

open class KmlMultiGeometry(geometries: List<Geometry<out Any>>) : MultiGeometry(geometries) {

    override fun getGeometryObject(): ArrayList<Geometry<out Any>> {
        val geometriesList = super.getGeometryObject()
        return ArrayList(geometriesList)
    }

    override fun toString(): String {
        val sb = StringBuilder(getGeometryType()).append("{")
        sb.append("\n geometries=").append(getGeometryObject())
        sb.append("\n}\n")
        return sb.toString()
    }
}
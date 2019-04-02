package com.anden.maki.data

open class MultiGeometry(val geometries: List<Geometry<out Any>>) : Geometry<Any> {

    private var geometryType = "MultiGeometry"

    override fun getGeometryType() = geometryType

    fun setGeometrytype(geometryType: String) {
        this.geometryType = geometryType
    }

    override fun getGeometryObject() = geometries

    override fun toString(): String {
        var typeString = "Geometries="
        if (geometryType == "MultiPoint") {
            typeString = "LineStrings="
        }
        if (geometryType == "MultiLineString") {
            typeString = "points="
        }
        if (geometryType == "MultiPolygon") {
            typeString = "Polygons="
        }

        val sb = StringBuilder(getGeometryType()).append("{")
        sb.append("\n $typeString").append(getGeometryObject())
        sb.append("\n}\n")
        return sb.toString()
    }
}
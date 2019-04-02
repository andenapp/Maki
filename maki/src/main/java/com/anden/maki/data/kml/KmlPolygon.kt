package com.anden.maki.data.kml

import com.anden.maki.data.Geometry
import com.google.android.gms.maps.model.LatLng

class KmlPolygon(val outerBoundaryCoordinates: List<LatLng>,
                 val innerBoundaryCoordinates: List<List<LatLng>>) : Geometry<List<List<LatLng>>> {

    companion object {
        const val GEOMETRY_TYPE = "Polygon"
    }

    override fun getGeometryType() = GEOMETRY_TYPE

    override fun getGeometryObject(): List<List<LatLng>> {
        val coordinates = mutableListOf<List<LatLng>>()
        coordinates.add(outerBoundaryCoordinates)
        //Polygon objects do not have to have inner holes
        coordinates.addAll(innerBoundaryCoordinates)

        return coordinates
    }

}
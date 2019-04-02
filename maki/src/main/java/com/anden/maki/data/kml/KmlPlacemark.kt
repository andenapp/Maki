package com.anden.maki.data.kml

import com.anden.maki.data.Feature
import com.anden.maki.data.Geometry
import com.google.android.gms.maps.model.PolygonOptions

class KmlPlacemark(id: String?,
                   geometry: Geometry<out Any>?,
                   properties: MutableMap<String, String>,
                   val inlineStyle: KmlStyle?) : Feature(id, geometry, properties) {

    /**
     * Gets a PolygonOption
     *
     * @return new PolygonOptions
     */
    fun getPolygonOptions(): PolygonOptions? {
        return inlineStyle?.getPolygonOptions()
    }

    /**
     * Gets a MarkerOption
     *
     * @return  A new MarkerOption
     */
    fun getMarkerOptions() = inlineStyle?.getMarkerOptions()

    /**
     * Gets a PolylineOption
     *
     * @return new PolylineOptions
     */
    fun getPolylineOptions() = inlineStyle?.getPolylineOptions()

    override fun toString(): String {
        val sb = StringBuilder("Placemark").append("{")
        sb.append("\n style id=").append(id)
        sb.append(",\n inline style=").append(inlineStyle)
        sb.append("\n}\n")
        return sb.toString()
    }
}
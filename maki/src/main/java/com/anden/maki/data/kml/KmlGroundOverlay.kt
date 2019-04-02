package com.anden.maki.data.kml

import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLngBounds

class KmlGroundOverlay(val imageUrl: String?,
                       val latLonBox: LatLngBounds,
                       val drawOrder: Float,
                       val visibility: Int,
                       val properties: HashMap<String, String> ,
                       val rotation: Float) {

    private val groundOverlayOptions: GroundOverlayOptions = GroundOverlayOptions()

    init {
        groundOverlayOptions.positionFromBounds(latLonBox)
        groundOverlayOptions.bearing(rotation)
        groundOverlayOptions.zIndex(drawOrder)
        groundOverlayOptions.visible(visibility != 0)
    }

    /**
     * Gets an iterable of the properties
     *
     * @return Iterable of the properties
     */
    fun getProperties(): Iterable<String> {
        return properties.keys
    }

    /**
     * Gets a property value
     *
     * @param keyValue key value of the property
     * @return Value of property
     */
    fun getProperty(keyValue: String): String? {
        return properties.get(keyValue)
    }

    /**
     * Returns a boolean value determining whether the ground overlay has a property
     *
     * @param keyValue Value to retrieve
     * @return True if the property exists, false otherwise
     */
    fun hasProperty(keyValue: String): Boolean {
        return properties.get(keyValue) != null
    }

    override fun toString(): String {
        val sb = StringBuilder("GroundOverlay").append("{")
        sb.append("\n properties=").append(properties)
        sb.append(",\n image url=").append(imageUrl)
        sb.append(",\n LatLngBox=").append(latLonBox)
        sb.append("\n}\n")
        return sb.toString()
    }

}
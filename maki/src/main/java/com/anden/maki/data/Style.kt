package com.anden.maki.data

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions

abstract class Style {

    internal var markerOptions: MarkerOptions = MarkerOptions()

    internal var polylineOptions: PolylineOptions = PolylineOptions()

    internal var polygonOptions: PolygonOptions = PolygonOptions()

    /**
     * Gets the rotation of a marker in degrees clockwise about the marker's anchor
     *
     * @return rotation of the Point
     */
    fun getRotation(): Float {
        return markerOptions.rotation
    }

    /**
     * Sets the rotation / heading of the Point in degrees clockwise about the marker's anchor
     *
     * @param rotation Decimal representation of the rotation value of the Point
     */
    fun setMarkerRotation(rotation: Float) {
        markerOptions.rotation(rotation)
    }

    /**
     * Sets the hotspot / anchor point of a marker
     *
     * @param x      x point of a marker position
     * @param y      y point of a marker position
     * @param xUnits units in which the x value is specified
     * @param yUnits units in which the y value is specified
     */
    fun setMarkerHotSpot(x: Float, y: Float, xUnits: String, yUnits: String) {
        var xAnchor = 0.5f
        var yAnchor = 1.0f

        // Set x coordinate
        if (xUnits == "fraction") {
            xAnchor = x
        }
        if (yUnits == "fraction") {
            yAnchor = y
        }

        markerOptions.anchor(xAnchor, yAnchor)
    }

    /**
     * Sets the width of the LineString in screen pixels
     *
     * @param width width value of the LineString
     */
    fun setLineStringWidth(width: Float) {
        polylineOptions.width(width)
    }

    /**
     * Sets the stroke width of the Polygon in screen pixels
     *
     * @param strokeWidth stroke width value of the Polygon
     */
    fun setPolygonStrokeWidth(strokeWidth: Float) {
        polygonOptions.strokeWidth(strokeWidth)
    }

    /**
     * Sets the fill color of the Polygon as a 32-bit ARGB color
     *
     * @param fillColor fill color value of the Polygon
     */
    fun setPolygonFillColor(fillColor: Int) {
        polygonOptions.fillColor(fillColor)
    }
}
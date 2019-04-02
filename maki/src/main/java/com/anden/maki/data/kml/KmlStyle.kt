package com.anden.maki.data.kml

import android.graphics.Color
import com.anden.maki.data.Style
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

class KmlStyle : Style() {

    companion object {

        private const val HSV_VALUES = 3

        private const val HUE_VALUE = 0


        private const val INITIAL_SCALE = 1
    }

    private val balloonOptions: MutableMap<String, String> = mutableMapOf()

    private val stylesSet: MutableSet<String> = mutableSetOf()

    private var fill = true

    private var outline = true

    private var iconUrl: String? = null

    private var scale: Double = INITIAL_SCALE.toDouble()

    private var styleId: String? = null

    private var iconRandomColorMode: Boolean = false

    private var lineRandomColorMode: Boolean = false

    private var polyRandomColorMode: Boolean = false

    private var markerColor: Float = 0f

    /**
     * Sets text found for an info window
     *
     * @param text Text for an info window
     */
    /* package */ internal fun setInfoWindowText(text: String) {
        balloonOptions["text"] = text
    }

    /**
     * Gets the id for the style
     *
     * @return Style Id, null otherwise
     */
    fun getStyleId(): String? {
        return styleId
    }

    /**
     * Sets id for a style
     *
     * @param styleId  Id for the style
     */
    fun setStyleId(styleId: String) {
        this.styleId = styleId
    }

    /**
     * Checks if a given style (for a marker, linestring or polygon) has been set
     *
     * @param style style to check if set
     * @return True if style was set, false otherwise
     */
    fun isStyleSet(style: String): Boolean {
        return stylesSet.contains(style)
    }

    /**
     * Gets whether the Polygon fill is set
     *
     * @return True if there is a fill for the polygon, false otherwise
     */
    fun hasFill(): Boolean {
        return fill
    }

    /**
     * Sets whether the Polygon has a fill
     *
     * @param fill True if the polygon fill is set, false otherwise
     */
    fun setFill(fill: Boolean) {
        this.fill = fill
    }

    /**
     * Gets the scale for a marker icon
     *
     * @return scale value
     */
    fun getIconScale(): Double {
        return scale
    }

    /**
     * Sets the scale for a marker icon
     *
     * @param scale scale value
     */
    fun setIconScale(scale: Double) {
        this.scale = scale
        stylesSet.add("iconScale")
    }

    /**
     * Gets whether the Polygon outline is set
     *
     * @return True if the polygon outline is set, false otherwise
     */
    fun hasOutline(): Boolean {
        return outline
    }

    /**
     * Gets whether a BalloonStyle has been set
     *
     * @return True if a BalloonStyle has been set, false otherwise
     */
    fun hasBalloonStyle(): Boolean {
        return balloonOptions.size > 0
    }

    /**
     * Sets whether the Polygon has an outline
     *
     * @param outline True if the polygon outline is set, false otherwise
     */
    /* package */ internal fun setOutline(outline: Boolean) {
        this.outline = outline
        stylesSet.add("outline")
    }

    /**
     * Gets the url for the marker icon
     *
     * @return Url for the marker icon, null otherwise
     */
    fun getIconUrl(): String? {
        return iconUrl
    }

    /**
     * Sets the url for the marker icon
     *
     * @param iconUrl Url for the marker icon
     */
    fun setIconUrl(iconUrl: String) {
        this.iconUrl = iconUrl
        stylesSet.add("iconUrl")
    }

    /**
     * Sets the fill color for a KML Polygon using a String
     *
     * @param color Fill color for a KML Polygon as a String
     */
    fun setFillColor(color: String) {
        // Add # to allow for outline color to be parsed correctly
        val polygonColorNum = Color.parseColor("#" + convertColor(color))
        setPolygonFillColor(polygonColorNum)
        stylesSet.add("fillColor")
    }

    /**
     * Sets the color for a marker
     *
     * @param color Color for a marker
     */
    fun setMarkerColor(color: String) {
        val integerColor = Color.parseColor("#" + convertColor(color))
        markerColor = getHueValue(integerColor)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor))
        stylesSet.add("markerColor")
    }

    /**
     * Gets the hue value from a color
     *
     * @param integerColor Integer representation of a color
     * @return Hue value from a color
     */
    private fun getHueValue(integerColor: Int): Float {
        val hsvValues = FloatArray(HSV_VALUES)
        Color.colorToHSV(integerColor, hsvValues)
        return hsvValues[HUE_VALUE]
    }

    /**
     * Converts a color format of the form AABBGGRR to AARRGGBB
     *
     * @param color Color of the form AABBGGRR
     * @return Color of the form AARRGGBB
     */
    private fun convertColor(color: String): String {
        var newColor: String
        if (color.length > 6) {
            newColor = (color.substring(0, 2) + color.substring(6, 8)
                    + color.substring(4, 6) + color.substring(2, 4))
        } else {
            newColor = color.substring(4, 6) + color.substring(2, 4) +
                    color.substring(0, 2)
        }
        // Maps exports KML colors with a leading 0 as a space.
        if (newColor.substring(0, 1) == " ") {
            newColor = "0" + newColor.substring(1, newColor.length)
        }
        return newColor
    }

    /**
     * Sets the rotation / heading for a marker
     *
     * @param heading Decimal representation of a rotation value
     */
    /* package */ internal fun setHeading(heading: Float) {
        setMarkerRotation(heading)
        stylesSet.add("heading")
    }

    /**
     * Sets the hotspot / anchor point of a marker
     *
     * @param x      x point of a marker position
     * @param y      y point of a marker position
     * @param xUnits units in which the x value is specified
     * @param yUnits units in which the y value is specified
     */
    /* package */ internal fun setHotSpot(x: Float, y: Float, xUnits: String, yUnits: String) {
        setMarkerHotSpot(x, y, xUnits, yUnits)
        stylesSet.add("hotSpot")
    }

    /**
     * Sets the color mode for a marker. A "random" color mode sets the color mode to true,
     * a "normal" colormode sets the color mode to false.
     *
     * @param colorMode A "random" or "normal" color mode
     */
    /* package */ internal fun setIconColorMode(colorMode: String) {
        iconRandomColorMode = colorMode == "random"
        stylesSet.add("iconColorMode")
    }

    /**
     * Checks whether the color mode for a marker is true / random
     *
     * @return True if the color mode is true, false otherwise
     */
    /* package */ internal fun isIconRandomColorMode(): Boolean {
        return iconRandomColorMode
    }

    /**
     * Sets the color mode for a polyline. A "random" color mode sets the color mode to true,
     * a "normal" colormode sets the color mode to false.
     *
     * @param colorMode A "random" or "normal" color mode
     */
    /* package */ internal fun setLineColorMode(colorMode: String) {
        lineRandomColorMode = colorMode == "random"
        stylesSet.add("lineColorMode")
    }

    /**
     * Checks whether the color mode for a polyline is true / random
     *
     * @return True if the color mode is true, false otherwise
     */
    fun isLineRandomColorMode(): Boolean {
        return lineRandomColorMode
    }

    /**
     * Sets the color mode for a polygon. A "random" color mode sets the color mode to true,
     * a "normal" colormode sets the color mode to false.
     *
     * @param colorMode A "random" or "normal" color mode
     */
    /* package */ internal fun setPolyColorMode(colorMode: String) {
        polyRandomColorMode = colorMode == "random"
        stylesSet.add("polyColorMode")
    }

    /**
     * Checks whether the color mode for a polygon is true / random
     *
     * @return True if the color mode is true, false otherwise
     */
    /* package */
    fun isPolyRandomColorMode(): Boolean {
        return polyRandomColorMode
    }

    /**
     * Sets the outline color for a Polyline and a Polygon using a String
     *
     * @param color Outline color for a Polyline and a Polygon represented as a String
     */
    fun setOutlineColor(color: String) {
        // Add # to allow for outline color to be parsed correctly
        polylineOptions.color(Color.parseColor("#" + convertColor(color)))
        polygonOptions.strokeColor(Color.parseColor("#" + convertColor(color)))
        stylesSet.add("outlineColor")
    }

    /**
     * Sets the line width for a Polyline and a Polygon
     *
     * @param width Line width for a Polyline and a Polygon
     */
    fun setWidth(width: Float) {
        setLineStringWidth(width)
        setPolygonStrokeWidth(width)
        stylesSet.add("width")
    }

    /**
     * Gets the balloon options
     *
     * @return Balloon Options
     */
    fun getBalloonOptions(): MutableMap<String, String> {
        return balloonOptions
    }

    /**
     * Creates a new marker option from given properties of an existing marker option
     *
     * @param originalMarkerOption An existing MarkerOption instance
     * @param iconRandomColorMode  True if marker color mode is random, false otherwise
     * @param markerColor          Color of the marker
     * @return A new MarkerOption
     */
    private fun createMarkerOptions(
        originalMarkerOption: MarkerOptions,
        iconRandomColorMode: Boolean, markerColor: Float
    ): MarkerOptions {
        val newMarkerOption = MarkerOptions()
        newMarkerOption.rotation(originalMarkerOption.rotation)
        newMarkerOption.anchor(originalMarkerOption.anchorU, originalMarkerOption.anchorV)
        if (iconRandomColorMode) {
            val hue = getHueValue(computeRandomColor(markerColor.toInt()))
            originalMarkerOption.icon(BitmapDescriptorFactory.defaultMarker(hue))
        }
        newMarkerOption.icon(originalMarkerOption.icon)
        return newMarkerOption
    }

    /**
     * Creates a new PolylineOption from given properties of an existing PolylineOption
     * @param originalPolylineOption An existing PolylineOption instance
     * @return A new PolylineOption
     */
    private fun createPolylineOptions(originalPolylineOption: PolylineOptions): PolylineOptions {
        val polylineOptions = PolylineOptions()
        polylineOptions.color(originalPolylineOption.color)
        polylineOptions.width(originalPolylineOption.width)
        return polylineOptions
    }

    /**
     * Creates a new PolygonOption from given properties of an existing PolygonOption
     * @param originalPolygonOption An existing PolygonOption instance
     * @param isFill Whether the fill for a polygon is set
     * @param isOutline Whether the outline for a polygon is set
     * @return  A new PolygonOption
     */
    private fun createPolygonOptions(
        originalPolygonOption: PolygonOptions,
        isFill: Boolean, isOutline: Boolean
    ): PolygonOptions {
        val polygonOptions = PolygonOptions()
        if (isFill) {
            polygonOptions.fillColor(originalPolygonOption.fillColor)
        }
        if (isOutline) {
            polygonOptions.strokeColor(originalPolygonOption.strokeColor)
            polygonOptions.strokeWidth(originalPolygonOption.strokeWidth)
        }
        return polygonOptions
    }

    /**
     * Gets a MarkerOption
     *
     * @return  A new MarkerOption
     */
    fun getMarkerOptions(): MarkerOptions {
        return createMarkerOptions(markerOptions, isIconRandomColorMode(), markerColor)
    }

    /**
     * Gets a PolylineOption
     *
     * @return new PolylineOptions
     */
    fun getPolylineOptions(): PolylineOptions {
        return createPolylineOptions(polylineOptions)
    }

    /**
     * Gets a PolygonOption
     *
     * @return new PolygonOptions
     */
    fun getPolygonOptions(): PolygonOptions {
        return createPolygonOptions(polygonOptions, fill, outline)
    }

    /**
     * Computes a random color given an integer. Algorithm to compute the random color can be
     * found in https://developers.google.com/kml/documentation/kmlreference#colormode
     *
     * @param color Color represented as an integer
     * @return Integer representing a random color
     */
    fun computeRandomColor(color: Int): Int {
        val random = Random()
        var red = Color.red(color)
        var green = Color.green(color)
        var blue = Color.blue(color)
        //Random number can only be computed in range [0, n)
        if (red != 0) {
            red = random.nextInt(red)
        }
        if (blue != 0) {
            blue = random.nextInt(blue)
        }
        if (green != 0) {
            green = random.nextInt(green)
        }
        return Color.rgb(red, green, blue)
    }

    override fun toString(): String {
        val sb = StringBuilder("Style").append("{")
        sb.append("\n balloon options=").append(balloonOptions)
        sb.append(",\n fill=").append(fill)
        sb.append(",\n outline=").append(outline)
        sb.append(",\n icon url=").append(iconUrl)
        sb.append(",\n scale=").append(scale)
        sb.append(",\n style id=").append(styleId)
        sb.append("\n}\n")
        return sb.toString()
    }

}
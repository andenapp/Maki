package com.anden.maki.parser

import com.anden.maki.data.kml.KmlContainer
import com.anden.maki.data.kml.KmlGroundOverlay
import com.anden.maki.data.kml.KmlPlacemark
import com.anden.maki.data.kml.KmlStyle
import com.google.android.gms.maps.model.GroundOverlay
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class KmlParser(private val parser: XmlPullParser) {

    companion object {

        private const val STYLE = "Style"

        private const val STYLE_MAP = "StyleMap"

        private const val PLACEMARK = "Placemark"

        private const val GROUND_OVERLAY = "GroundOverlay"

        private const val CONTAINER_REGEX = "Folder|Document"

        private const val UNSUPPORTED_REGEX = "altitude|altitudeModeGroup|altitudeMode|" +
                "begin|bottomFov|cookie|displayName|displayMode|displayMode|end|expires|extrude|" +
                "flyToView|gridOrigin|httpQuery|leftFov|linkDescription|linkName|linkSnippet|" +
                "listItemType|maxSnippetLines|maxSessionLength|message|minAltitude|minFadeExtent|" +
                "minLodPixels|minRefreshPeriod|maxAltitude|maxFadeExtent|maxLodPixels|maxHeight|" +
                "maxWidth|near|NetworkLink|NetworkLinkControl|overlayXY|range|refreshMode|" +
                "refreshInterval|refreshVisibility|rightFov|roll|rotationXY|screenXY|shape|sourceHref|" +
                "state|targetHref|tessellate|tileSize|topFov|viewBoundScale|viewFormat|viewRefreshMode|" +
                "viewRefreshTime|when"

        /**
         * Skips tags from START TAG to END TAG
         * @param parser    XmlPullParser
         */
        @Throws(XmlPullParserException::class, IOException::class)
        fun skip(parser: XmlPullParser) {
            if (parser.eventType != START_TAG) {
                throw IllegalStateException()
            }
            var depth = 1
            while (depth != 0) {
                when (parser.next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        }

    }

    var placemarks: MutableMap<KmlPlacemark, Any?> = hashMapOf()

    var containers: MutableList<KmlContainer> = mutableListOf()

    var styles: MutableMap<String?, KmlStyle> = hashMapOf()

    var styleMaps: MutableMap<String, String> = hashMapOf()

    var groundOverlays: MutableMap<KmlGroundOverlay, GroundOverlay?> = hashMapOf()

    /**
     * Parses the KML file and stores the created KmlStyle and KmlPlacemark
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parseKml() {
        var eventType = parser.eventType
        while (eventType != END_DOCUMENT) {
            if (eventType == START_TAG) {
                if (parser.name.matches(UNSUPPORTED_REGEX.toRegex())) {
                    skip(parser)
                }
                if (parser.name.matches(CONTAINER_REGEX.toRegex())) {
                    containers.add(
                        KmlContainerParser.createContainer(parser)
                    )
                }
                if (parser.name == STYLE) {
                    val style = KmlStyleParser.createStyle(parser)
                    styles[style.getStyleId()] = style
                }
                if (parser.name == STYLE_MAP) {
                    styleMaps.putAll(KmlStyleParser.createStyleMap(parser))
                }
                if (parser.name == PLACEMARK) {
                    placemarks[KmlFeatureParser.createPlacemark(parser)] = null
                }
                if (parser.name == GROUND_OVERLAY) {
                    groundOverlays[KmlFeatureParser.createGroundOverlay(parser)] = null
                }
            }
            eventType = parser.next()

        }
        //Need to put an empty new style
        styles[null] = KmlStyle()
    }
}
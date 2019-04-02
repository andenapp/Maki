package com.anden.maki.parser

import com.anden.maki.data.kml.KmlContainer
import com.anden.maki.data.kml.KmlGroundOverlay
import com.anden.maki.data.kml.KmlPlacemark
import com.anden.maki.data.kml.KmlStyle
import com.google.android.gms.maps.model.GroundOverlay
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap

class KmlContainerParser {

    companion object {

        private val PROPERTY_REGEX = "name|description|visibility|open|address|phoneNumber"

        private val CONTAINER_REGEX = "Folder|Document"

        private val PLACEMARK = "Placemark"

        private val STYLE = "Style"

        private val STYLE_MAP = "StyleMap"

        private val EXTENDED_DATA = "ExtendedData"

        private val GROUND_OVERLAY = "GroundOverlay"

        private val UNSUPPORTED_REGEX = "altitude|altitudeModeGroup|altitudeMode|" +
                "begin|bottomFov|cookie|displayName|displayMode|displayMode|end|expires|extrude|flyToView|" +
                "gridOrigin|httpQuery|leftFov|linkDescription|linkName|linkSnippet|listItemType|maxSnippetLines|" +
                "maxSessionLength|message|minAltitude|minFadeExtent|minLodPixels|minRefreshPeriod|maxAltitude|" +
                "maxFadeExtent|maxLodPixels|maxHeight|maxWidth|near|overlayXY|range|" +
                "refreshMode|refreshInterval|refreshVisibility|rightFov|roll|rotationXY|screenXY|shape|" +
                "sourceHref|state|targetHref|tessellate|tileSize|topFov|viewBoundScale|viewFormat|" +
                "viewRefreshMode|viewRefreshTime|when"

        /**
         * Obtains a Container object (created if a Document or Folder start tag is read by the
         * XmlPullParser) and assigns specific elements read from the XmlPullParser to the container.
         */

        /* package */
        @Throws(XmlPullParserException::class, IOException::class)
        fun createContainer(parser: XmlPullParser): KmlContainer {
            return assignPropertiesToContainer(parser)
        }

        /**
         * Creates a new KmlContainer objects and assigns specific elements read from the XmlPullParser
         * to the new KmlContainer.
         *
         * @param parser XmlPullParser object reading from a KML file
         * @return KmlContainer object with properties read from the XmlPullParser
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun assignPropertiesToContainer(parser: XmlPullParser): KmlContainer {
            val startTag = parser.name
            var containerId: String? = null
            val containerProperties = HashMap<String, String>()
            val containerStyles = HashMap<String?, KmlStyle>()
            val containerPlacemarks = HashMap<KmlPlacemark, Any>()
            val nestedContainers = ArrayList<KmlContainer>()
            val containerStyleMaps = HashMap<String, String>()
            val containerGroundOverlays = HashMap<KmlGroundOverlay, GroundOverlay?>()

            if (parser.getAttributeValue(null, "id") != null) {
                containerId = parser.getAttributeValue(null, "id")
            }

            parser.next()
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == startTag)) {
                if (eventType == START_TAG) {
                    if (parser.name.matches(UNSUPPORTED_REGEX.toRegex())) {
                        KmlParser.skip(parser)
                    } else if (parser.name.matches(CONTAINER_REGEX.toRegex())) {
                        nestedContainers.add(
                            assignPropertiesToContainer(
                                parser
                            )
                        )
                    } else if (parser.name.matches(PROPERTY_REGEX.toRegex())) {
                        containerProperties[parser.name] = parser.nextText()
                    } else if (parser.name == STYLE_MAP) {
                        setContainerStyleMap(
                            parser,
                            containerStyleMaps
                        )
                    } else if (parser.name == STYLE) {
                        setContainerStyle(
                            parser,
                            containerStyles
                        )
                    } else if (parser.name == PLACEMARK) {
                        setContainerPlacemark(
                            parser,
                            containerPlacemarks as HashMap<KmlPlacemark, Any?>
                        )
                    } else if (parser.name == EXTENDED_DATA) {
                        setExtendedDataProperties(
                            parser,
                            containerProperties
                        )
                    } else if (parser.name == GROUND_OVERLAY) {
                        containerGroundOverlays[KmlFeatureParser.createGroundOverlay(parser)] = null
                    }
                }
                eventType = parser.next()
            }

            return KmlContainer(containerProperties, containerPlacemarks)
            //return KmlContainer(containerProperties, containerStyles, containerPlacemarks as HashMap<KmlPlacemark, Any>, containerStyleMaps, nestedContainers, containerGroundOverlays, containerId)
        }

        /**
         * Creates a new style map and assigns values from the XmlPullParser parser
         * and stores it into the container.
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun setContainerStyleMap(
            parser: XmlPullParser,
            containerStyleMap: HashMap<String, String>
        ) {
            containerStyleMap.putAll(KmlStyleParser.createStyleMap(parser))
        }

        /**
         * Assigns properties given as an extended data element, which are obtained from an
         * XmlPullParser and stores it in a container, Untyped data only, no simple data
         * or schema, and entity replacements of the form $[dataName] are unsupported.
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun setExtendedDataProperties(
            parser: XmlPullParser,
            mContainerProperties: HashMap<String, String>
        ) {
            var propertyKey: String? = null
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.name == EXTENDED_DATA)) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name == "Data") {
                        propertyKey = parser.getAttributeValue(null, "name")
                    } else if (parser.name == "value" && propertyKey != null) {
                        mContainerProperties[propertyKey] = parser.nextText()
                        propertyKey = null
                    }
                }
                eventType = parser.next()
            }
        }

        /**
         * Creates a new default Kml Style with a specified ID (given as an attribute value in the
         * start tag) and assigns specific elements read from the XmlPullParser to the Style. A new
         * style is not created if it does not have an ID.
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun setContainerStyle(
            parser: XmlPullParser,
            containerStyles: HashMap<String?, KmlStyle>
        ) {
            if (parser.getAttributeValue(null, "id") != null) {
                val style = KmlStyleParser.createStyle(parser)
                val styleId = style.getStyleId()
                containerStyles[styleId] = style
            }
        }

        /**
         * Creates a new placemark object  and assigns specific elements read from the XmlPullParser
         * to the Placemark and stores this into the given Container.
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun setContainerPlacemark(parser: XmlPullParser, containerPlacemarks: HashMap<KmlPlacemark, Any?>) {
            containerPlacemarks[KmlFeatureParser.createPlacemark(parser)] = null
        }


    }

}
package com.anden.maki.parser

import com.anden.maki.data.kml.KmlStyle
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.util.HashMap

import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG

class KmlStyleParser {

    companion object {

        private const val STYLE_TAG = "styleUrl"

        private const val ICON_STYLE_HEADING = "heading"

        private const val ICON_STYLE_URL = "Icon"

        private const val ICON_STYLE_SCALE = "scale"

        private const val ICON_STYLE_HOTSPOT = "hotSpot"

        private const val COLOR_STYLE_COLOR = "color"

        private const val COLOR_STYLE_MODE = "colorMode"

        private const val STYLE_MAP_KEY = "key"

        private const val STYLE_MAP_NORMAL_STYLE = "normal"

        private const val LINE_STYLE_WIDTH = "width"

        private const val POLY_STYLE_OUTLINE = "outline"

        private const val POLY_STYLE_FILL = "fill"

        /**
         * Parses the IconStyle, LineStyle and PolyStyle tags into a KmlStyle object
         */
        /* package */
        @Throws(IOException::class, XmlPullParserException::class)
        fun createStyle(parser: XmlPullParser): KmlStyle {
            val styleProperties = KmlStyle()
            setStyleId(parser.getAttributeValue(null, "id"), styleProperties)
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "Style")) {
                if (eventType == START_TAG) {
                    if (parser.name == "IconStyle") {
                        createIconStyle(parser, styleProperties)
                    } else if (parser.name == "LineStyle") {
                        createLineStyle(parser, styleProperties)
                    } else if (parser.name == "PolyStyle") {
                        createPolyStyle(parser, styleProperties)
                    } else if (parser.name == "BalloonStyle") {
                        createBalloonStyle(parser, styleProperties)
                    }
                }
                eventType = parser.next()
            }
            return styleProperties
        }

        /**
         *
         * @param styleProperties
         */
        private fun setStyleId(id: String?, styleProperties: KmlStyle) {
            if (id != null) {
                // Append # to a local styleUrl
                val styleId = "#$id"
                styleProperties.setStyleId(styleId)
            }
        }

        /**
         * Recieves input from an XMLPullParser and assigns relevant properties to a KmlStyle.
         *
         * @param style Style to apply properties to
         * @return true if icon style has been set
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createIconStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "IconStyle")) {
                if (eventType == START_TAG) {
                    if (parser.name == ICON_STYLE_HEADING) {
                        style.setHeading(java.lang.Float.parseFloat(parser.nextText()))
                    } else if (parser.name == ICON_STYLE_URL) {
                        setIconUrl(parser, style)
                    } else if (parser.name == ICON_STYLE_HOTSPOT) {
                        setIconHotSpot(parser, style)
                    } else if (parser.name == ICON_STYLE_SCALE) {
                        style.setIconScale(java.lang.Double.parseDouble(parser.nextText()))
                    } else if (parser.name == COLOR_STYLE_COLOR) {
                        style.setMarkerColor(parser.nextText())
                    } else if (parser.name == COLOR_STYLE_MODE) {
                        style.setIconColorMode(parser.nextText())
                    }
                }
                eventType = parser.next()
            }
        }

        /**
         * Parses the StyleMap property and stores the id and the normal style tag
         */
        /* package */
        @Throws(XmlPullParserException::class, IOException::class)
        fun createStyleMap(parser: XmlPullParser): HashMap<String, String> {
            val styleMaps = HashMap<String, String>()
            // Indicates if a normal style is to be stored
            var isNormalStyleMapValue: Boolean? = false
            // Append # to style id
            val styleId = "#" + parser.getAttributeValue(null, "id")
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "StyleMap")) {
                if (eventType == START_TAG) {
                    if (parser.name == STYLE_MAP_KEY && parser.nextText() == STYLE_MAP_NORMAL_STYLE) {
                        isNormalStyleMapValue = true
                    } else if (parser.name == STYLE_TAG && isNormalStyleMapValue!!) {
                        styleMaps[styleId] = parser.nextText()
                        isNormalStyleMapValue = false
                    }
                }
                eventType = parser.next()
            }
            return styleMaps
        }

        /**
         * Sets relevant styling properties to the KmlStyle object that are found in the IconStyle tag
         * Supported tags include scale, heading, Icon, href, hotSpot
         *
         * @param style Style object to add properties to
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createBalloonStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "BalloonStyle")) {
                if (eventType == START_TAG && parser.name == "text") {
                    style.setInfoWindowText(parser.nextText())
                }
                eventType = parser.next()
            }
        }

        /**
         * Sets the icon url for the style
         *
         * @param style Style to set the icon url to
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun setIconUrl(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == ICON_STYLE_URL)) {
                if (eventType == START_TAG && parser.name == "href") {
                    style.setIconUrl(parser.nextText())
                }
                eventType = parser.next()
            }
        }

        /**
         * Sets the hot spot for the icon
         *
         * @param style Style object to apply hotspot properties to
         */
        private fun setIconHotSpot(parser: XmlPullParser, style: KmlStyle) {
            val xValue: Float?
            val yValue: Float?
            val xUnits: String
            val yUnits: String
            xValue = java.lang.Float.parseFloat(parser.getAttributeValue(null, "x"))
            yValue = java.lang.Float.parseFloat(parser.getAttributeValue(null, "y"))
            xUnits = parser.getAttributeValue(null, "xunits")
            yUnits = parser.getAttributeValue(null, "yunits")
            style.setHotSpot(xValue, yValue, xUnits, yUnits)
        }

        /**
         * Sets relevant styling properties to the KmlStyle object that are found in the LineStyle tag
         * Supported tags include color, width
         *
         * @param style Style object to add properties to
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createLineStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "LineStyle")) {
                if (eventType == START_TAG) {
                    if (parser.name == COLOR_STYLE_COLOR) {
                        style.setOutlineColor(parser.nextText())
                    } else if (parser.name == LINE_STYLE_WIDTH) {
                        style.setWidth(java.lang.Float.valueOf(parser.nextText()))
                    } else if (parser.name == COLOR_STYLE_MODE) {
                        style.setLineColorMode(parser.nextText())
                    }
                }
                eventType = parser.next()
            }
        }

        /**
         * Sets relevant styling properties to the KmlStyle object that are found in the PolyStyle tag
         * Supported tags include color, outline, fill
         *
         * @param style Style object to add properties to
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createPolyStyle(parser: XmlPullParser, style: KmlStyle) {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "PolyStyle")) {
                if (eventType == START_TAG) {
                    if (parser.name == COLOR_STYLE_COLOR) {
                        style.setFillColor(parser.nextText())
                    } else if (parser.name == POLY_STYLE_OUTLINE) {
                        style.setOutline(KmlBooleanParser.parseBoolean(parser.nextText()))
                    } else if (parser.name == POLY_STYLE_FILL) {
                        style.setFill(KmlBooleanParser.parseBoolean(parser.nextText()))
                    } else if (parser.name == COLOR_STYLE_MODE) {
                        style.setPolyColorMode(parser.nextText())
                    }
                }
                eventType = parser.next()
            }
        }
    }

}

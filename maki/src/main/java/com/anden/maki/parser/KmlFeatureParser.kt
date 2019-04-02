package com.anden.maki.parser

import com.anden.maki.data.Geometry
import com.anden.maki.data.kml.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParser.END_TAG
import org.xmlpull.v1.XmlPullParser.START_TAG
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class KmlFeatureParser {

    companion object {

        private val GEOMETRY_REGEX = "Point|LineString|Polygon|MultiGeometry|Track|MultiTrack"

        private val LONGITUDE_INDEX = 0

        private val LATITUDE_INDEX = 1

        private val ALTITUDE_INDEX = 2

        private val PROPERTY_REGEX = "name|description|drawOrder|visibility|open|address|phoneNumber"

        private val BOUNDARY_REGEX = "outerBoundaryIs|innerBoundaryIs"

        private val EXTENDED_DATA = "ExtendedData"

        private val STYLE_URL_TAG = "styleUrl"

        private val STYLE_TAG = "Style"

        private val COMPASS_REGEX = "north|south|east|west"

        private val LAT_LNG_ALT_SEPARATOR = ","

        /**
         * Internal helper class to store latLng and altitude in a single object.
         * This allows to parse [lon,lat,altitude] tuples in KML files more efficiently.
         * Note that altitudes are generally optional so they can be null.
         */
        private class LatLngAlt internal constructor(val latLng: LatLng, val altitude: Double?)

        /**
         * Creates a new Placemark object (created if a Placemark start tag is read by the
         * XmlPullParser and if a Geometry tag is contained within the Placemark tag)
         * and assigns specific elements read from the parser to the Placemark.
         */
        /* package */
        @Throws(IOException::class, XmlPullParserException::class)
        fun createPlacemark(parser: XmlPullParser): KmlPlacemark {
            var styleId: String? = null
            var inlineStyle: KmlStyle? = null
            val properties = HashMap<String, String>()
            var geometry: Geometry<out Any>? = null
            var eventType = parser.eventType

            while (!(eventType == END_TAG && parser.name == "Placemark")) {
                if (eventType == START_TAG) {
                    if (parser.name == STYLE_URL_TAG) {
                        styleId = parser.nextText()
                    } else if (parser.name.matches(GEOMETRY_REGEX.toRegex())) {
                        geometry = createGeometry(parser, parser.name)
                    } else if (parser.name.matches(PROPERTY_REGEX.toRegex())) {
                        properties[parser.name] = parser.nextText()
                    } else if (parser.name == EXTENDED_DATA) {
                        properties.putAll(setExtendedDataProperties(parser))
                    } else if (parser.name == STYLE_TAG) {
                        inlineStyle = KmlStyleParser.createStyle(parser)
                    }
                }
                eventType = parser.next()
            }
            return KmlPlacemark(styleId, geometry, properties, inlineStyle)
        }

        /**
         * Creates a new GroundOverlay object (created if a GroundOverlay tag is read by the
         * XmlPullParser) and assigns specific elements read from the parser to the GroundOverlay
         */
        /* package */
        @Throws(IOException::class, XmlPullParserException::class)
        fun createGroundOverlay(parser: XmlPullParser): KmlGroundOverlay {
            var drawOrder = 0.0f
            var rotation = 0.0f
            var visibility = 1
            var imageUrl: String? = null
            val latLonBox: LatLngBounds
            val properties = HashMap<String, String>()
            val compassPoints = HashMap<String, Double>()

            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "GroundOverlay")) {
                if (eventType == START_TAG) {
                    if (parser.name == "Icon") {
                        imageUrl = getImageUrl(parser)
                    } else if (parser.name == "drawOrder") {
                        drawOrder = java.lang.Float.parseFloat(parser.nextText())
                    } else if (parser.name == "visibility") {
                        visibility = Integer.parseInt(parser.nextText())
                    } else if (parser.name == "ExtendedData") {
                        properties.putAll(setExtendedDataProperties(parser))
                    } else if (parser.name == "rotation") {
                        rotation = getRotation(parser)
                    } else if (parser.name.matches(PROPERTY_REGEX.toRegex()) || parser.name == "color") {
                        properties[parser.name] = parser.nextText()
                    } else if (parser.name.matches(COMPASS_REGEX.toRegex())) {
                        compassPoints[parser.name] = java.lang.Double.parseDouble(parser.nextText())
                    }
                }
                eventType = parser.next()
            }
            latLonBox = createLatLngBounds(
                compassPoints["north"], compassPoints["south"],
                compassPoints["east"], compassPoints["west"]
            )
            return KmlGroundOverlay(imageUrl, latLonBox, drawOrder, visibility, properties, rotation)
        }

        @Throws(IOException::class, XmlPullParserException::class)
        private fun getRotation(parser: XmlPullParser): Float {
            return -java.lang.Float.parseFloat(parser.nextText())
        }

        /**
         * Retrieves a url from the "href" tag nested within an "Icon" tag, read by
         * the XmlPullParser.
         *
         * @return An image url
         */
        @Throws(IOException::class, XmlPullParserException::class)
        private fun getImageUrl(parser: XmlPullParser): String? {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "Icon")) {
                if (eventType == START_TAG && parser.name == "href") {
                    return parser.nextText()
                }
                eventType = parser.next()
            }
            return null
        }

        /**
         * Creates a new Geometry object (Created if "Point", "LineString", "Polygon" or
         * "MultiGeometry" tag is detected by the XmlPullParser)
         *
         * @param geometryType Type of geometry object to create
         */
        @Throws(IOException::class, XmlPullParserException::class)
        private fun createGeometry(parser: XmlPullParser, geometryType: String): Geometry<out Any>? {
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == geometryType)) {
                if (eventType == START_TAG) {
                    if (parser.name == "Point") {
                        return createPoint(parser)
                    } else if (parser.name == "LineString") {
                        return createLineString(parser)
                    } else if (parser.name == "Track") {
                        return createTrack(parser)
                    } else if (parser.name == "Polygon") {
                        return createPolygon(parser)
                    } else if (parser.name == "MultiGeometry") {
                        return createMultiGeometry(parser)
                    } else if (parser.name == "MultiTrack") {
                        return createMultiTrack(parser)
                    }
                }
                eventType = parser.next()
            }
            return null
        }

        /**
         * Adds untyped name value pairs parsed from the ExtendedData
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun setExtendedDataProperties(parser: XmlPullParser): HashMap<String, String> {
            val properties = HashMap<String, String>()
            var propertyKey: String? = null
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.name == EXTENDED_DATA)) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name == "Data") {
                        propertyKey = parser.getAttributeValue(null, "name")
                    } else if (parser.name == "value" && propertyKey != null) {
                        properties[propertyKey] = parser.nextText()
                        propertyKey = null
                    }
                }
                eventType = parser.next()
            }
            return properties
        }

        /**
         * Creates a new KmlPoint object
         *
         * @return KmlPoint object
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createPoint(parser: XmlPullParser): KmlPoint {
            var latLngAlt: LatLngAlt? = null
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "Point")) {
                if (eventType == START_TAG && parser.name == "coordinates") {
                    latLngAlt = convertToLatLngAlt(parser.nextText())
                }
                eventType = parser.next()
            }
            return KmlPoint(latLngAlt!!.latLng, latLngAlt.altitude)
        }

        /**
         * Creates a new KmlLineString object
         *
         * @return KmlLineString object
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createLineString(parser: XmlPullParser): KmlLineString {
            val coordinates = ArrayList<LatLng>()
            val altitudes = ArrayList<Double>()
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "LineString")) {
                if (eventType == START_TAG && parser.name == "coordinates") {
                    val latLngAlts = convertToLatLngAltArray(parser.nextText())
                    for (latLngAlt in latLngAlts) {
                        coordinates.add(latLngAlt.latLng)
                        if (latLngAlt.altitude != null) {
                            altitudes.add(latLngAlt.altitude)
                        }
                    }
                }
                eventType = parser.next()
            }
            return KmlLineString(coordinates, altitudes)
        }

        /**
         * Creates a new KmlTrack object
         *
         * @return KmlTrack object
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createTrack(parser: XmlPullParser): KmlTrack {
            val iso8601 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            iso8601.timeZone = TimeZone.getTimeZone("UTC")
            val latLngs = ArrayList<LatLng>()
            val altitudes = ArrayList<Double>()
            val timestamps = ArrayList<Long>()
            val properties = HashMap<String, String>()
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "Track")) {
                if (eventType == START_TAG) {
                    if (parser.name == "coord") {
                        val coordinateString = parser.nextText()
                        //fields are separated by spaces instead of commas
                        val latLngAlt = convertToLatLngAlt(coordinateString, " ")
                        latLngs.add(latLngAlt.latLng)
                        if (latLngAlt.altitude != null) {
                            altitudes.add(latLngAlt.altitude)
                        }
                    } else if (parser.name == "when") {
                        try {
                            val dateString = parser.nextText()
                            val date = iso8601.parse(dateString)
                            val millis = date.time

                            timestamps.add(millis)
                        } catch (e: ParseException) {
                            throw XmlPullParserException("Invalid date", parser, e)
                        }

                    } else if (parser.name == EXTENDED_DATA) {
                        properties.putAll(setExtendedDataProperties(parser))
                    }
                }
                eventType = parser.next()
            }
            return KmlTrack(latLngs, altitudes, timestamps, properties)
        }

        /**
         * Creates a new KmlPolygon object. Parses only one outer boundary and no or many inner
         * boundaries containing the coordinates.
         *
         * @return KmlPolygon object
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createPolygon(parser: XmlPullParser): KmlPolygon {
            // Indicates if an outer boundary needs to be defined
            var isOuterBoundary: Boolean? = false
            var outerBoundary: List<LatLng> = ArrayList()
            val innerBoundaries = ArrayList<List<LatLng>>()
            var eventType = parser.eventType
            while (!(eventType == END_TAG && parser.name == "Polygon")) {
                if (eventType == START_TAG) {
                    if (parser.name.matches(BOUNDARY_REGEX.toRegex())) {
                        isOuterBoundary = parser.name == "outerBoundaryIs"
                    } else if (parser.name == "coordinates") {
                        if (isOuterBoundary!!) {
                            outerBoundary = convertToLatLngArray(parser.nextText())
                        } else {
                            innerBoundaries.add(convertToLatLngArray(parser.nextText()))
                        }
                    }
                }
                eventType = parser.next()
            }
            return KmlPolygon(outerBoundary, innerBoundaries)
        }

        /**
         * Creates a new KmlMultiGeometry object
         *
         * @return KmlMultiGeometry object
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createMultiGeometry(parser: XmlPullParser): KmlMultiGeometry {
            val geometries = ArrayList<Geometry<out Any>>()
            // Get next otherwise have an infinite loop
            var eventType = parser.next()
            while (!(eventType == END_TAG && parser.name == "MultiGeometry")) {
                if (eventType == START_TAG && parser.name.matches(GEOMETRY_REGEX.toRegex())) {
                    createGeometry(parser, parser.name)?.let { geometries.add(it) }
                }
                eventType = parser.next()
            }
            return KmlMultiGeometry(geometries)
        }

        /**
         * Creates a new KmlMultiTrack object
         *
         * @return KmlMultiTrack object
         */
        @Throws(XmlPullParserException::class, IOException::class)
        private fun createMultiTrack(parser: XmlPullParser): KmlMultiTrack {
            val tracks = ArrayList<KmlTrack>()
            // Get next otherwise have an infinite loop
            var eventType = parser.next()
            while (!(eventType == END_TAG && parser.name == "MultiTrack")) {
                if (eventType == START_TAG && parser.name.matches("Track".toRegex())) {
                    tracks.add(createTrack(parser))
                }
                eventType = parser.next()
            }
            return KmlMultiTrack(tracks)
        }

        /**
         * Convert a string of coordinates into an array of LatLngs
         *
         * @param coordinatesString coordinates string to convert from
         * @return array of LatLng objects created from the given coordinate string array
         */
        private fun convertToLatLngArray(coordinatesString: String): ArrayList<LatLng> {
            val latLngAltsArray = convertToLatLngAltArray(coordinatesString)
            val coordinatesArray = ArrayList<LatLng>()
            for (latLngAlt in latLngAltsArray) {
                coordinatesArray.add(latLngAlt.latLng)
            }
            return coordinatesArray
        }

        /**
         * Convert a string of coordinates into an array of LatLngAlts
         *
         * @param coordinatesString coordinates string to convert from
         * @return array of LatLngAlt objects created from the given coordinate string array
         */
        private fun convertToLatLngAltArray(coordinatesString: String): ArrayList<LatLngAlt> {
            val latLngAltsArray = ArrayList<LatLngAlt>()
            // Need to trim to avoid whitespace around the coordinates such as tabs
            val coordinates =
                coordinatesString.trim { it <= ' ' }.split("(\\s+)".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            for (coordinate in coordinates) {
                latLngAltsArray.add(convertToLatLngAlt(coordinate))
            }
            return latLngAltsArray
        }

        /**
         * Convert a string coordinate from a string into a LatLngAlt object
         *
         * @param coordinateString coordinate string to convert from
         * @return LatLngAlt object created from given coordinate string
         */
        private fun convertToLatLngAlt(coordinateString: String): LatLngAlt {
            return convertToLatLngAlt(coordinateString, LAT_LNG_ALT_SEPARATOR)
        }

        /**
         * Convert a string coordinate from a string into a LatLngAlt object
         *
         * @param coordinateString coordinate string to convert from
         * @param separator separator to use when splitting coordinates
         * @return LatLngAlt object created from given coordinate string
         */
        private fun convertToLatLngAlt(coordinateString: String, separator: String): LatLngAlt {
            val coordinate = coordinateString.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val lat = java.lang.Double.parseDouble(coordinate[LATITUDE_INDEX])
            val lon = java.lang.Double.parseDouble(coordinate[LONGITUDE_INDEX])
            val alt = if (coordinate.size > 2) java.lang.Double.parseDouble(coordinate[ALTITUDE_INDEX]) else null
            val latLng = LatLng(lat, lon)
            return LatLngAlt(latLng, alt)
        }

        /**
         * Given a set of four latLng coordinates, creates a LatLng Bound
         *
         * @param north North coordinate of the bounding box
         * @param south South coordinate of the bounding box
         * @param east  East coordinate of the bounding box
         * @param west  West coordinate of the bounding box
         */
        private fun createLatLngBounds(north: Double?, south: Double?, east: Double?, west: Double?): LatLngBounds {
            val southWest = LatLng(south!!, west!!)
            val northEast = LatLng(north!!, east!!)
            return LatLngBounds(southWest, northEast)
        }

    }

}
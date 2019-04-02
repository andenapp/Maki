package com.anden.maki.ui

import android.content.Context
import android.util.Xml
import com.anden.maki.data.kml.KmlLineString
import com.anden.maki.data.kml.KmlPlacemark
import com.anden.maki.data.kml.KmlPoint
import com.anden.maki.parser.KmlParser
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class KmlLayer private constructor(private val parser: KmlParser,
                                   private val lineColor: Int?,
                                   private val markerBitmapDescriptor: BitmapDescriptor?) {

    private var polyline: Polyline? = null

    init {
        parser.parseKml()
    }

    fun addToMap(googleMap: GoogleMap) {
        parser.containers.get(0).placemarks.forEach {

            val placemark = it.key

            val geometry = placemark.geometry

            if (geometry is KmlPoint) {
                addMarkerToMap(googleMap, placemark, geometry)
            } else if (geometry is KmlLineString) {
                polyline = addPolylineToMap(googleMap, geometry)
            }
        }
    }

    private fun addMarkerToMap(googleMap: GoogleMap, placemark: KmlPlacemark, point: KmlPoint): Marker {
        val markerOptions = MarkerOptions()

        placemark.properties["name"]?.let { markerOptions.title(it) }
        placemark.properties["description"]?.let { markerOptions.snippet(it) }

        markerOptions.position(point.coordinates)

        markerBitmapDescriptor?.let { markerOptions.icon(it) }

        return googleMap.addMarker(markerOptions)
    }


    private fun addPolylineToMap(googleMap: GoogleMap, line: KmlLineString): Polyline {
        val polylineOptions = PolylineOptions()

        lineColor?.let { polylineOptions.color(it) }

        polylineOptions.addAll(line.coordinates)

       return googleMap.addPolyline(polylineOptions)
    }

    fun removeToMap() {
        polyline?.remove()
    }

    data class Builder(val context: Context,
                       val kmlResourceId: Int,
                       var lineColor: Int? = null,
                       var markerBitmapDescriptor: BitmapDescriptor? = null) {

        fun lineColor(color: Int) = apply { lineColor = color }

        fun markerBitmapDescriptor(bitmapDescriptor: BitmapDescriptor) = apply { markerBitmapDescriptor = bitmapDescriptor }

        fun build() : KmlLayer {

            val inputStream = context.resources.openRawResource(kmlResourceId)
            val xmlParser = Xml.newPullParser()
            xmlParser.setInput(inputStream, null)

            return KmlLayer(KmlParser(xmlParser), lineColor, markerBitmapDescriptor)
        }

    }
}
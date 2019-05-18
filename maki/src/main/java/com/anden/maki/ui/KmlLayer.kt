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
                                   var markerAnchorU: Float,
                                   var markerAnchorV: Float,
                                   var markersTag: Any?,
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
        markerOptions.anchor(markerAnchorU, markerAnchorV)

        placemark.properties["name"]?.let { markerOptions.title(it) }
        placemark.properties["description"]?.let { markerOptions.snippet(it) }

        markerOptions.position(point.coordinates)

        markerBitmapDescriptor?.let { markerOptions.icon(it) }

        val marker = googleMap.addMarker(markerOptions)
        marker.tag = markersTag

        return marker
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

    data class Builder(private val context: Context,
                       private val kmlResourceId: Int,
                       private var lineColor: Int? = null,
                       private var markerAnchorU: Float = 1f,
                       private var markerAnchorV: Float = 1f,
                       private var markersTag: Any? = null,
                       private var markerBitmapDescriptor: BitmapDescriptor? = null) {

        fun lineColor(color: Int) = apply { lineColor = color }

        fun markerAnchor(u: Float, v: Float) = apply {
            markerAnchorU = u
            markerAnchorV = v
        }

        fun markersTag(tag: Any) = apply { markersTag = tag }

        fun markerBitmapDescriptor(bitmapDescriptor: BitmapDescriptor) = apply { markerBitmapDescriptor = bitmapDescriptor }

        fun build() : KmlLayer {

            val inputStream = context.resources.openRawResource(kmlResourceId)
            val xmlParser = Xml.newPullParser()
            xmlParser.setInput(inputStream, null)

            return KmlLayer(KmlParser(xmlParser), lineColor, markerAnchorU, markerAnchorV, markersTag, markerBitmapDescriptor)
        }

    }
}
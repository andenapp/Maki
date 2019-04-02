package com.anden.maki.data.kml

class KmlContainer(
    val properties: HashMap<String, String>,
    val placemarks: HashMap<KmlPlacemark, Any>
) {
}
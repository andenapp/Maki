package com.anden.maki.data.kml

import com.google.android.gms.maps.model.LatLng

class KmlTrack(coordinates: List<LatLng>,
               altitudes: List<Double>,
               val timestamps: List<Long>,
               val properties: HashMap<String, String>) : KmlLineString(coordinates, altitudes)
package com.anden.maki.data.kml

import com.anden.maki.data.Geometry
import com.google.android.gms.maps.model.LatLng

class KmlMultiTrack(val tracks: List<KmlTrack>) : KmlMultiGeometry(createGeometries(tracks)) {

    companion object {

        private fun createGeometries(tracks: List<KmlTrack>?): List<Geometry<List<LatLng>>> {
            val geometries = mutableListOf<Geometry<List<LatLng>>>()

            if (tracks == null) {
                throw IllegalArgumentException("Tracks cannot be null")
            }

            for (track in tracks) {
                geometries.add(track)
            }

            return geometries
        }

    }

}
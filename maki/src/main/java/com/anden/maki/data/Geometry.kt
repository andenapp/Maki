package com.anden.maki.data

interface Geometry<T> {

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    abstract fun getGeometryType(): String

    /**
     * Gets the stored KML Geometry object
     *
     * @return geometry object
     */
    abstract fun getGeometryObject(): T
}
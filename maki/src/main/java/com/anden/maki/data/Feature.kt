package com.anden.maki.data

import java.util.*

open class Feature(val id: String?,
                   val geometry: Geometry<out Any>?,
                   val properties: MutableMap<String, String>) : Observable() {

    /**
     * Returns all the stored property keys
     *
     * @return iterable of property keys
     */
    fun getPropertyKeys(): Iterable<String> {
        return properties.keys
    }

    /**
     * Gets the property entry set
     *
     * @return property entry set
     */
    fun getProperties(): Iterable<*> {
        return properties.entries
    }

    /**
     * Gets the value for a stored property
     *
     * @param property key of the property
     * @return value of the property if its key exists, otherwise null
     */
    fun getProperty(property: String): String? {
        return properties.get(property)
    }

    /**
     * Checks whether the given property key exists
     *
     * @param property key of the property to check
     * @return true if property key exists, false otherwise
     */
    fun hasProperty(property: String): Boolean {
        return properties.containsKey(property)
    }

    /**
     * Gets whether the placemark has properties
     *
     * @return true if there are properties in the properties map, false otherwise
     */
    fun hasProperties(): Boolean {
        return properties.size > 0
    }

    /**
     * Checks if the geometry is assigned
     *
     * @return true if feature contains geometry object, otherwise null
     */
    fun hasGeometry(): Boolean {
        return geometry != null
    }

    /**
     * Store a new property key and value
     *
     * @param property      key of the property to store
     * @param propertyValue value of the property to store
     * @return previous value with the same key, otherwise null if the key didn't exist
     */
    protected fun setProperty(property: String, propertyValue: String): String? {
        return properties.put(property, propertyValue)
    }

    /**
     * Removes a given property
     *
     * @param property key of the property to remove
     * @return value of the removed property or null if there was no corresponding key
     */
    protected fun removeProperty(property: String): String? {
        return properties.remove(property)
    }

}
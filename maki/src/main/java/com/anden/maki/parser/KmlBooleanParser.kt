package com.anden.maki.parser

class KmlBooleanParser {

    companion object {
        fun parseBoolean(text: String): Boolean {
            return "1" == text || "true" == text
        }
    }
}
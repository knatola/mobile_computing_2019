package com.example.whatisup.src.data.model

data class PhysicalActivity(
    val type: Int,
    var duration: Long
) {
    override fun toString(): String {
        return "type: $type, duration: $duration"
    }
}
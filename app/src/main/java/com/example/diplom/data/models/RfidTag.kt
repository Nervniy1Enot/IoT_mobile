package com.example.diplom.models

data class RfidTag(
    val uid: String,
    val ownerName: String,
    val isActive: Boolean,
    val dateAdded: String
)
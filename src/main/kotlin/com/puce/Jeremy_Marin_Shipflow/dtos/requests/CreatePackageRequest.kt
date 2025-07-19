package com.puce.Jeremy_Marin_Shipflow.dtos.requests

import com.puce.Jeremy_Marin_Shipflow.models.entities.Type

data class CreatePackageRequest(
    val type: Type,
    val weight: Float,
    val description: String,
    val cityFrom: String,
    val cityTo: String
)
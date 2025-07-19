package com.puce.Jeremy_Marin_Shipflow.dtos.responses

import com.puce.Jeremy_Marin_Shipflow.models.entities.Status
import com.puce.Jeremy_Marin_Shipflow.models.entities.Type
import java.time.LocalDateTime

data class PackageResponse(
    val id: Long,
    val trackingId: String,
    val type: Type,
    val description: String,
    val weight: Float,
    val currentStatus: Status,
    val cityFrom: String,
    val cityTo: String,
    val createdAt: LocalDateTime,
    val estimatedDeliveryDate: LocalDateTime
)
package com.puce.Jeremy_Marin_Shipflow.dtos.responses

import java.time.LocalDateTime

data class UpdateStatusResponse(
    val message: String,
    val trackingId: String,
    val newStatus: String,
    val updatedAt: LocalDateTime
)
package com.puce.Jeremy_Marin_Shipflow.dtos.responses

import com.puce.Jeremy_Marin_Shipflow.models.entities.Status
import java.time.LocalDateTime

data class PackageEventResponse(
    val id: Long,
    val status: Status,
    val comment: String?,
    val createdAt: LocalDateTime
)
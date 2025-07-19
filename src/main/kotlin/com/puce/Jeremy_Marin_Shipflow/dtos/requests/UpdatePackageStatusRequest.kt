package com.puce.Jeremy_Marin_Shipflow.dtos.requests

import com.puce.Jeremy_Marin_Shipflow.models.entities.Status

data class UpdatePackageStatusRequest(
    val status: Status,
    val comment: String? = null
)
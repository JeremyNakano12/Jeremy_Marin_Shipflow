package com.puce.Jeremy_Marin_Shipflow.dtos.responses

data class CreatePackageResponse(
    val message: String,
    val trackingId: String,
    val packageInfo: PackageResponse
)
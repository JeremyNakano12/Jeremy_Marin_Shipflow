package com.puce.Jeremy_Marin_Shipflow.dtos.responses

data class PackageDetailResponse(
    val packageInfo: PackageResponse,
    val statusHistory: List<PackageEventResponse>
)
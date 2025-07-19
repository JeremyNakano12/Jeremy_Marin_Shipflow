package com.puce.Jeremy_Marin_Shipflow.services

import com.puce.Jeremy_Marin_Shipflow.dtos.requests.CreatePackageRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.requests.UpdatePackageStatusRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.CreatePackageResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageDetailResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.UpdateStatusResponse

interface PackageService {

    fun createPackage(request: CreatePackageRequest): CreatePackageResponse

    fun getAllPackages(): List<PackageResponse>

    fun getPackageByTrackingId(trackingId: String): PackageResponse

    fun getPackageWithHistory(trackingId: String): PackageDetailResponse

    fun updatePackageStatus(trackingId: String, request: UpdatePackageStatusRequest): UpdateStatusResponse
}
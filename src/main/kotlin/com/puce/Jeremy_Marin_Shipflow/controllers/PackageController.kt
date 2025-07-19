package com.puce.Jeremy_Marin_Shipflow.controllers

import com.puce.Jeremy_Marin_Shipflow.dtos.requests.CreatePackageRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.requests.UpdatePackageStatusRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.CreatePackageResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageDetailResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.UpdateStatusResponse
import com.puce.Jeremy_Marin_Shipflow.services.PackageService
import com.puce.Jeremy_Marin_Shipflow.utils.Routes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_URL + Routes.PACKAGES)
class PackageController(
    private val packageService: PackageService
) {

    @PostMapping
    fun createPackage(@RequestBody request: CreatePackageRequest): ResponseEntity<CreatePackageResponse> {
        val response = packageService.createPackage(request)
        return ResponseEntity(response, HttpStatus.CREATED)
    }


    @GetMapping
    fun getAllPackages(): ResponseEntity<List<PackageResponse>> {
        val packages = packageService.getAllPackages()
        return ResponseEntity(packages, HttpStatus.OK)
    }

    @GetMapping(Routes.TRACKING_ID)
    fun getPackageByTrackingId(@PathVariable trackingId: String): ResponseEntity<PackageResponse> {
        val packageResponse = packageService.getPackageByTrackingId(trackingId)
        return ResponseEntity(packageResponse, HttpStatus.OK)
    }

    @GetMapping(Routes.PACKAGE_HISTORY)
    fun getPackageWithHistory(@PathVariable trackingId: String): ResponseEntity<PackageDetailResponse> {
        val packageDetail = packageService.getPackageWithHistory(trackingId)
        return ResponseEntity(packageDetail, HttpStatus.OK)
    }

    @PutMapping(Routes.PACKAGE_STATUS)
    fun updatePackageStatus(
        @PathVariable trackingId: String,
        @RequestBody request: UpdatePackageStatusRequest
    ): ResponseEntity<UpdateStatusResponse> {
        val response = packageService.updatePackageStatus(trackingId, request)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
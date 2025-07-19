// PackageServiceImpl.kt
package com.puce.Jeremy_Marin_Shipflow.services.impl

import com.puce.Jeremy_Marin_Shipflow.dtos.requests.CreatePackageRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.requests.UpdatePackageStatusRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.CreatePackageResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageDetailResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageResponse
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.UpdateStatusResponse
import com.puce.Jeremy_Marin_Shipflow.exceptions.*
import com.puce.Jeremy_Marin_Shipflow.mappers.PackageEventMapper
import com.puce.Jeremy_Marin_Shipflow.mappers.PackageMapper
import com.puce.Jeremy_Marin_Shipflow.models.entities.Status
import com.puce.Jeremy_Marin_Shipflow.repositories.PackageEventRepository
import com.puce.Jeremy_Marin_Shipflow.repositories.PackageRepository
import com.puce.Jeremy_Marin_Shipflow.services.PackageService
import com.puce.Jeremy_Marin_Shipflow.services.StatusValidationService
import com.puce.Jeremy_Marin_Shipflow.services.TrackingService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class PackageServiceImpl(
    private val packageRepository: PackageRepository,
    private val packageEventRepository: PackageEventRepository,
    private val trackingService: TrackingService,
    private val statusValidationService: StatusValidationService,
    private val packageMapper: PackageMapper,
    private val packageEventMapper: PackageEventMapper
) : PackageService {

    override fun createPackage(request: CreatePackageRequest): CreatePackageResponse {
        if (request.cityFrom.equals(request.cityTo, ignoreCase = true)) {
            throw InvalidCityException("Origin city cannot be the same as destination city")
        }

        if (request.description.length > 50) {
            throw DescriptionTooLongException("Description exceeds maximum length of 50 characters. Current length: ${request.description.length}")
        }

        val trackingId = trackingService.generateTrackingId()

        val packageEntity = packageMapper.toEntity(request, trackingId)

        val savedPackage = packageRepository.save(packageEntity)

        val initialEvent = packageEventMapper.toEntity(
            UpdatePackageStatusRequest(Status.PENDING, "Package registered and pending processing"),
            savedPackage
        )
        packageEventRepository.save(initialEvent)

        return packageMapper.toCreateResponse(savedPackage)
    }

    @Transactional(readOnly = true)
    override fun getAllPackages(): List<PackageResponse> {
        return packageRepository.findAll().map { packageMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getPackageByTrackingId(trackingId: String): PackageResponse {
        val packageEntity = packageRepository.findByTrackingId(trackingId)
            .orElseThrow { PackageNotFoundException("Package with tracking ID '$trackingId' not found") }

        return packageMapper.toResponse(packageEntity)
    }

    @Transactional(readOnly = true)
    override fun getPackageWithHistory(trackingId: String): PackageDetailResponse {
        val packageEntity = packageRepository.findByTrackingId(trackingId)
            .orElseThrow { PackageNotFoundException("Package with tracking ID '$trackingId' not found") }

        return packageMapper.toDetailResponse(packageEntity, packageEventMapper)
    }

    override fun updatePackageStatus(trackingId: String, request: UpdatePackageStatusRequest): UpdateStatusResponse {
        val packageEntity = packageRepository.findByTrackingId(trackingId)
            .orElseThrow { PackageNotFoundException("Package with tracking ID '$trackingId' not found") }

        if (!statusValidationService.isValidTransition(packageEntity.currentStatus, request.status)) {
            throw InvalidStatusTransitionException(
                "Invalid status transition from ${packageEntity.currentStatus} to ${request.status}"
            )
        }

        if (request.status == Status.DELIVERED) {
            val hasBeenInTransit = packageEntity.events.any { it.status == Status.IN_TRANSIT }
            if (!hasBeenInTransit) {
                throw BusinessRuleException("Package can only be marked as DELIVERED if it has been IN_TRANSIT previously")
            }
        }

        packageEntity.currentStatus = request.status
        packageRepository.save(packageEntity)

        val statusEvent = packageEventMapper.toEntity(request, packageEntity)
        packageEventRepository.save(statusEvent)

        return UpdateStatusResponse(
            message = "Package status updated successfully",
            trackingId = trackingId,
            newStatus = request.status.name,
            updatedAt = LocalDateTime.now()
        )
    }
}
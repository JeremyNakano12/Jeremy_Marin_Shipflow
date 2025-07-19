package com.puce.Jeremy_Marin_Shipflow.mappers

import com.puce.Jeremy_Marin_Shipflow.dtos.requests.UpdatePackageStatusRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.PackageEventResponse
import com.puce.Jeremy_Marin_Shipflow.models.entities.Package
import com.puce.Jeremy_Marin_Shipflow.models.entities.PackageEvent
import org.springframework.stereotype.Component

@Component
class PackageEventMapper {

    fun toEntity(request: UpdatePackageStatusRequest, packageEntity: Package): PackageEvent {
        return PackageEvent(
            status = request.status,
            comment = request.comment,
            packageEntity = packageEntity
        )
    }

    fun toResponse(entity: PackageEvent): PackageEventResponse {
        return PackageEventResponse(
            id = entity.id,
            status = entity.status,
            comment = entity.comment,
            createdAt = entity.createdAt
        )
    }
}
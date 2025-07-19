package com.puce.Jeremy_Marin_Shipflow.services.impl

import com.puce.Jeremy_Marin_Shipflow.models.entities.Status
import com.puce.Jeremy_Marin_Shipflow.services.StatusValidationService
import org.springframework.stereotype.Service

@Service
class StatusValidationServiceImpl : StatusValidationService {

    private val validTransitions = mapOf(
        Status.PENDING to listOf(Status.IN_TRANSIT),
        Status.IN_TRANSIT to listOf(Status.DELIVERED, Status.ON_HOLD, Status.CANCELLED),
        Status.ON_HOLD to listOf(Status.IN_TRANSIT, Status.CANCELLED),
        Status.DELIVERED to emptyList(),
        Status.CANCELLED to emptyList()
    )

    override fun isValidTransition(currentStatus: Status, newStatus: Status): Boolean {
        return validTransitions[currentStatus]?.contains(newStatus) ?: false
    }

}
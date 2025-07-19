package com.puce.Jeremy_Marin_Shipflow.services

import com.puce.Jeremy_Marin_Shipflow.models.entities.Status

interface StatusValidationService {

    fun isValidTransition(currentStatus: Status, newStatus: Status): Boolean

}
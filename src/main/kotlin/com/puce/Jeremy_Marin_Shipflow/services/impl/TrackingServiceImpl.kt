// TrackingServiceImpl.kt
package com.puce.Jeremy_Marin_Shipflow.services.impl

import com.puce.Jeremy_Marin_Shipflow.repositories.PackageRepository
import com.puce.Jeremy_Marin_Shipflow.services.TrackingService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Service
class TrackingServiceImpl(
    private val packageRepository: PackageRepository
) : TrackingService {

    override fun generateTrackingId(): String {
        var trackingId: String
        do {
            trackingId = generateUniqueId()
        } while (packageRepository.existsByTrackingId(trackingId))

        return trackingId
    }

    private fun generateUniqueId(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val randomSuffix = Random.nextInt(1000, 9999)
        return "PKG$timestamp$randomSuffix"
    }
}
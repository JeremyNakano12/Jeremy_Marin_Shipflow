package com.puce.Jeremy_Marin_Shipflow.repositories

import com.puce.Jeremy_Marin_Shipflow.models.entities.Package
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PackageRepository : JpaRepository<Package, Long> {

    fun findByTrackingId(trackingId: String): Optional<Package>

    fun existsByTrackingId(trackingId: String): Boolean
}
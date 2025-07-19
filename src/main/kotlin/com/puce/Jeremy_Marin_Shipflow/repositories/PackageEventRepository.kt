package com.puce.Jeremy_Marin_Shipflow.repositories

import com.puce.Jeremy_Marin_Shipflow.models.entities.PackageEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PackageEventRepository: JpaRepository<PackageEvent, Long>
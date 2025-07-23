package com.puce.Jeremy_Marin_Shipflow.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.puce.Jeremy_Marin_Shipflow.dtos.requests.CreatePackageRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.requests.UpdatePackageStatusRequest
import com.puce.Jeremy_Marin_Shipflow.dtos.responses.*
import com.puce.Jeremy_Marin_Shipflow.exceptions.InvalidCityException
import com.puce.Jeremy_Marin_Shipflow.exceptions.PackageNotFoundException
import com.puce.Jeremy_Marin_Shipflow.exceptions.InvalidStatusTransitionException
import com.puce.Jeremy_Marin_Shipflow.exceptions.DescriptionTooLongException
import com.puce.Jeremy_Marin_Shipflow.models.entities.Status
import com.puce.Jeremy_Marin_Shipflow.models.entities.Type
import com.puce.Jeremy_Marin_Shipflow.services.PackageService
import com.puce.Jeremy_Marin_Shipflow.utils.Routes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(PackageController::class)
@Import(PackageMockConfig::class)
class PackageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var packageService: PackageService

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private val BASE_URL = Routes.BASE_URL + Routes.PACKAGES

    @Test
    fun `should create package successfully`() {
        val request = CreatePackageRequest(
            type = Type.SMALL_BOX,
            weight = 2.5f,
            description = "Test package",
            cityFrom = "Quito",
            cityTo = "Guayaquil"
        )

        val packageResponse = PackageResponse(
            id = 1L,
            trackingId = "PKG202501231234567890",
            type = Type.SMALL_BOX,
            description = "Test package",
            weight = 2.5f,
            currentStatus = Status.PENDING,
            cityFrom = "Quito",
            cityTo = "Guayaquil",
            createdAt = LocalDateTime.now(),
            estimatedDeliveryDate = LocalDateTime.now().plusDays(5)
        )

        val createResponse = CreatePackageResponse(
            message = "Package created successfully",
            trackingId = "PKG202501231234567890",
            packageInfo = packageResponse
        )

        `when`(packageService.createPackage(request)).thenReturn(createResponse)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isCreated() }
            jsonPath("$.message") { value("Package created successfully") }
            jsonPath("$.trackingId") { value("PKG202501231234567890") }
            jsonPath("$.packageInfo.type") { value("SMALL_BOX") }
            jsonPath("$.packageInfo.weight") { value(2.5) }
            jsonPath("$.packageInfo.description") { value("Test package") }
            jsonPath("$.packageInfo.cityFrom") { value("Quito") }
            jsonPath("$.packageInfo.cityTo") { value("Guayaquil") }
            jsonPath("$.packageInfo.currentStatus") { value("PENDING") }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun `should return 400 when creating package with same cities`() {
        val request = CreatePackageRequest(
            type = Type.DOCUMENT,
            weight = 0.1f,
            description = "Test document",
            cityFrom = "Quito",
            cityTo = "Quito"
        )

        `when`(packageService.createPackage(request))
            .thenThrow(InvalidCityException("Origin city cannot be the same as destination city"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()

        assertEquals(400, result.response.status)
    }

    @Test
    fun `should return 400 when creating package with description too long`() {
        val request = CreatePackageRequest(
            type = Type.FRAGILE,
            weight = 1.0f,
            description = "This is a very long description that exceeds the maximum allowed length of 50 characters for testing purposes",
            cityFrom = "Quito",
            cityTo = "Guayaquil"
        )

        `when`(packageService.createPackage(request))
            .thenThrow(DescriptionTooLongException("Description exceeds maximum length of 50 characters. Current length: ${request.description.length}"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isBadRequest() }
        }.andReturn()

        assertEquals(400, result.response.status)
    }

    @Test
    fun `should return all packages`() {
        val packages = listOf(
            PackageResponse(
                id = 1L,
                trackingId = "PKG202501231234567890",
                type = Type.DOCUMENT,
                description = "Document 1",
                weight = 0.1f,
                currentStatus = Status.PENDING,
                cityFrom = "Quito",
                cityTo = "Guayaquil",
                createdAt = LocalDateTime.now(),
                estimatedDeliveryDate = LocalDateTime.now().plusDays(5)
            ),
            PackageResponse(
                id = 2L,
                trackingId = "PKG202501231234567891",
                type = Type.SMALL_BOX,
                description = "Box 1",
                weight = 2.0f,
                currentStatus = Status.IN_TRANSIT,
                cityFrom = "Cuenca",
                cityTo = "Quito",
                createdAt = LocalDateTime.now(),
                estimatedDeliveryDate = LocalDateTime.now().plusDays(3)
            )
        )

        `when`(packageService.getAllPackages()).thenReturn(packages)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$[0].trackingId") { value("PKG202501231234567890") }
                jsonPath("$[0].description") { value("Document 1") }
                jsonPath("$[0].currentStatus") { value("PENDING") }
                jsonPath("$[1].trackingId") { value("PKG202501231234567891") }
                jsonPath("$[1].description") { value("Box 1") }
                jsonPath("$[1].currentStatus") { value("IN_TRANSIT") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun `should return package by tracking id`() {
        val trackingId = "PKG202501231234567890"
        val packageResponse = PackageResponse(
            id = 1L,
            trackingId = trackingId,
            type = Type.FRAGILE,
            description = "Fragile item",
            weight = 3.0f,
            currentStatus = Status.DELIVERED,
            cityFrom = "Quito",
            cityTo = "Guayaquil",
            createdAt = LocalDateTime.now(),
            estimatedDeliveryDate = LocalDateTime.now().plusDays(5)
        )

        `when`(packageService.getPackageByTrackingId(trackingId)).thenReturn(packageResponse)

        val result = mockMvc.get("$BASE_URL/$trackingId")
            .andExpect {
                status { isOk() }
                jsonPath("$.trackingId") { value(trackingId) }
                jsonPath("$.description") { value("Fragile item") }
                jsonPath("$.type") { value("FRAGILE") }
                jsonPath("$.weight") { value(3.0) }
                jsonPath("$.currentStatus") { value("DELIVERED") }
                jsonPath("$.cityFrom") { value("Quito") }
                jsonPath("$.cityTo") { value("Guayaquil") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun `should return 404 when package not found by tracking id`() {
        val trackingId = "NONEXISTENT123"

        `when`(packageService.getPackageByTrackingId(trackingId))
            .thenThrow(PackageNotFoundException("Package with tracking ID '$trackingId' not found"))

        val result = mockMvc.get("$BASE_URL/$trackingId")
            .andExpect {
                status { isNotFound() }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun `should return package with history`() {
        val trackingId = "PKG202501231234567890"
        val packageResponse = PackageResponse(
            id = 1L,
            trackingId = trackingId,
            type = Type.DOCUMENT,
            description = "Document with history",
            weight = 0.2f,
            currentStatus = Status.IN_TRANSIT,
            cityFrom = "Quito",
            cityTo = "Guayaquil",
            createdAt = LocalDateTime.now(),
            estimatedDeliveryDate = LocalDateTime.now().plusDays(5)
        )

        val statusHistory = listOf(
            PackageEventResponse(
                id = 1L,
                status = Status.PENDING,
                comment = "Package registered and pending processing",
                createdAt = LocalDateTime.now().minusDays(1)
            ),
            PackageEventResponse(
                id = 2L,
                status = Status.IN_TRANSIT,
                comment = "Package in transit",
                createdAt = LocalDateTime.now()
            )
        )

        val packageDetail = PackageDetailResponse(
            packageInfo = packageResponse,
            statusHistory = statusHistory
        )

        `when`(packageService.getPackageWithHistory(trackingId)).thenReturn(packageDetail)

        val result = mockMvc.get("$BASE_URL/$trackingId/history")
            .andExpect {
                status { isOk() }
                jsonPath("$.packageInfo.trackingId") { value(trackingId) }
                jsonPath("$.packageInfo.description") { value("Document with history") }
                jsonPath("$.statusHistory[0].status") { value("PENDING") }
                jsonPath("$.statusHistory[0].comment") { value("Package registered and pending processing") }
                jsonPath("$.statusHistory[1].status") { value("IN_TRANSIT") }
                jsonPath("$.statusHistory[1].comment") { value("Package in transit") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun `should return 404 when getting history for non-existent package`() {
        val trackingId = "NONEXISTENT456"

        `when`(packageService.getPackageWithHistory(trackingId))
            .thenThrow(PackageNotFoundException("Package with tracking ID '$trackingId' not found"))

        val result = mockMvc.get("$BASE_URL/$trackingId/history")
            .andExpect {
                status { isNotFound() }
            }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun `should update package status successfully`() {
        val trackingId = "PKG202501231234567890"
        val request = UpdatePackageStatusRequest(
            status = Status.IN_TRANSIT,
            comment = "Package picked up and in transit"
        )

        val updateResponse = UpdateStatusResponse(
            message = "Package status updated successfully",
            trackingId = trackingId,
            newStatus = "IN_TRANSIT",
            updatedAt = LocalDateTime.now()
        )

        `when`(packageService.updatePackageStatus(trackingId, request)).thenReturn(updateResponse)

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/$trackingId/status") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isOk() }
            jsonPath("$.message") { value("Package status updated successfully") }
            jsonPath("$.trackingId") { value(trackingId) }
            jsonPath("$.newStatus") { value("IN_TRANSIT") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun `should return 404 when updating status for non-existent package`() {
        val trackingId = "NONEXISTENT789"
        val request = UpdatePackageStatusRequest(
            status = Status.DELIVERED,
            comment = "Package delivered"
        )

        `when`(packageService.updatePackageStatus(trackingId, request))
            .thenThrow(PackageNotFoundException("Package with tracking ID '$trackingId' not found"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/$trackingId/status") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isNotFound() }
        }.andReturn()

        assertEquals(404, result.response.status)
    }

    @Test
    fun `should return 409 when invalid status transition`() {
        val trackingId = "PKG202501231234567890"
        val request = UpdatePackageStatusRequest(
            status = Status.DELIVERED,
            comment = "Trying to deliver without transit"
        )

        `when`(packageService.updatePackageStatus(trackingId, request))
            .thenThrow(InvalidStatusTransitionException("Invalid status transition from PENDING to DELIVERED"))

        val json = objectMapper.writeValueAsString(request)

        val result = mockMvc.put("$BASE_URL/$trackingId/status") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isConflict() }
        }.andReturn()

        assertEquals(409, result.response.status)
    }
}

@TestConfiguration
class PackageMockConfig {
    @Bean
    fun packageService(): PackageService = mock(PackageService::class.java)
}
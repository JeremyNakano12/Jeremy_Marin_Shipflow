package com.puce.Jeremy_Marin_Shipflow.services

import com.puce.Jeremy_Marin_Shipflow.models.entities.Status
import com.puce.Jeremy_Marin_Shipflow.services.impl.StatusValidationServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StatusValidationServiceTest {

    private lateinit var service: StatusValidationServiceImpl

    @BeforeEach
    fun setUp() {
        service = StatusValidationServiceImpl()
    }

    @Test
    fun `should allow transition from PENDING to IN_TRANSIT`() {
        val result = service.isValidTransition(Status.PENDING, Status.IN_TRANSIT)
        assertTrue(result)
    }

    @Test
    fun `should not allow transition from PENDING to DELIVERED`() {
        val result = service.isValidTransition(Status.PENDING, Status.DELIVERED)
        assertFalse(result)
    }

    @Test
    fun `should not allow transition from PENDING to ON_HOLD`() {
        val result = service.isValidTransition(Status.PENDING, Status.ON_HOLD)
        assertFalse(result)
    }

    @Test
    fun `should not allow transition from PENDING to CANCELLED`() {
        val result = service.isValidTransition(Status.PENDING, Status.CANCELLED)
        assertFalse(result)
    }

    @Test
    fun `should not allow transition from PENDING to PENDING`() {
        val result = service.isValidTransition(Status.PENDING, Status.PENDING)
        assertFalse(result)
    }

    @Test
    fun `should allow transition from IN_TRANSIT to DELIVERED`() {
        val result = service.isValidTransition(Status.IN_TRANSIT, Status.DELIVERED)
        assertTrue(result)
    }

    @Test
    fun `should allow transition from IN_TRANSIT to ON_HOLD`() {
        val result = service.isValidTransition(Status.IN_TRANSIT, Status.ON_HOLD)
        assertTrue(result)
    }

    @Test
    fun `should allow transition from IN_TRANSIT to CANCELLED`() {
        val result = service.isValidTransition(Status.IN_TRANSIT, Status.CANCELLED)
        assertTrue(result)
    }

    @Test
    fun `should not allow transition from IN_TRANSIT to PENDING`() {
        val result = service.isValidTransition(Status.IN_TRANSIT, Status.PENDING)
        assertFalse(result)
    }

    @Test
    fun `should not allow transition from IN_TRANSIT to IN_TRANSIT`() {
        val result = service.isValidTransition(Status.IN_TRANSIT, Status.IN_TRANSIT)
        assertFalse(result)
    }

    @Test
    fun `should allow transition from ON_HOLD to IN_TRANSIT`() {
        val result = service.isValidTransition(Status.ON_HOLD, Status.IN_TRANSIT)
        assertTrue(result)
    }

    @Test
    fun `should allow transition from ON_HOLD to CANCELLED`() {
        val result = service.isValidTransition(Status.ON_HOLD, Status.CANCELLED)
        assertTrue(result)
    }

    @Test
    fun `should not allow transition from ON_HOLD to PENDING`() {
        val result = service.isValidTransition(Status.ON_HOLD, Status.PENDING)
        assertFalse(result)
    }

    @Test
    fun `should not allow transition from ON_HOLD to DELIVERED`() {
        val result = service.isValidTransition(Status.ON_HOLD, Status.DELIVERED)
        assertFalse(result)
    }

    @Test
    fun `should not allow transition from ON_HOLD to ON_HOLD`() {
        val result = service.isValidTransition(Status.ON_HOLD, Status.ON_HOLD)
        assertFalse(result)
    }

    @Test
    fun `should not allow any transition from DELIVERED`() {
        assertFalse(service.isValidTransition(Status.DELIVERED, Status.PENDING))
        assertFalse(service.isValidTransition(Status.DELIVERED, Status.IN_TRANSIT))
        assertFalse(service.isValidTransition(Status.DELIVERED, Status.ON_HOLD))
        assertFalse(service.isValidTransition(Status.DELIVERED, Status.CANCELLED))
        assertFalse(service.isValidTransition(Status.DELIVERED, Status.DELIVERED))
    }

    @Test
    fun `should not allow any transition from CANCELLED`() {
        assertFalse(service.isValidTransition(Status.CANCELLED, Status.PENDING))
        assertFalse(service.isValidTransition(Status.CANCELLED, Status.IN_TRANSIT))
        assertFalse(service.isValidTransition(Status.CANCELLED, Status.ON_HOLD))
        assertFalse(service.isValidTransition(Status.CANCELLED, Status.DELIVERED))
        assertFalse(service.isValidTransition(Status.CANCELLED, Status.CANCELLED))
    }
}
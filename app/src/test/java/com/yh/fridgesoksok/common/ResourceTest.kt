package com.yh.fridgesoksok.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ResourceTest {

    @Test
    fun `flowWithResource는 Loading 후 Success를 방출해야 한다`() = runBlocking {
        // Given
        val testData = "test data"

        // When
        val flow = flowWithResource { testData }
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(testData, (results[1] as Resource.Success).data)
    }

    @Test
    fun `flowWithResource는 예외 발생 시 Loading 후 Error를 방출해야 한다`() = runBlocking {
        // Given
        val errorMessage = "Test error"

        // When
        val flow = flowWithResource<String> {
            throw Exception(errorMessage)
        }
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals(errorMessage, (results[1] as Resource.Error).message)
    }

    @Test
    fun `flowWithResource는 취소 예외를 Error로 바꾸지 않고 전파해야 한다`() = runBlocking {
        // Given
        val results = mutableListOf<Resource<String>>()

        // When
        val thrown = try {
            flowWithResource<String> { throw CancellationException("cancelled") }
                .collect { results += it }
            null
        } catch (e: CancellationException) {
            e
        }

        // Then
        assertTrue(thrown is CancellationException)
        assertFalse(results.any { it is Resource.Error })
    }

    @Test
    fun `flowWithResource는 HttpException 발생 시 상태 코드를 포함해야 한다`() = runBlocking {
        // Given
        val statusCode = 404
        val httpException = HttpException(Response.error<Any>(statusCode, okhttp3.ResponseBody.create(null, "")))

        // When
        val flow = flowWithResource<String> {
            throw httpException
        }
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals(statusCode, (results[1] as Resource.Error).status)
    }

    @Test
    fun `flowWithResource는 null을 성공적으로 처리해야 한다`() = runBlocking {
        // When
        val flow = flowWithResource<String?> { null }
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(null, (results[1] as Resource.Success).data)
    }

    @Test
    fun `flowWithResource는 복잡한 객체를 처리해야 한다`() = runBlocking {
        // Given
        data class TestObject(val id: String, val name: String, val count: Int)
        val testObject = TestObject("1", "test", 100)

        // When
        val flow = flowWithResource { testObject }
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        val successData = (results[1] as Resource.Success).data
        assertEquals("1", successData?.id)
        assertEquals("test", successData?.name)
        assertEquals(100, successData?.count)
    }

    @Test
    fun `flowWithResource는 suspend 함수를 올바르게 호출해야 한다`() = runBlocking {
        // Given
        suspend fun suspendFunction(): String {
            kotlinx.coroutines.delay(10)
            return "delayed result"
        }

        // When
        val flow = flowWithResource { suspendFunction() }
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals("delayed result", (results[1] as Resource.Success).data)
    }

    @Test
    fun `Resource Success는 올바른 데이터를 포함해야 한다`() {
        // Given
        val testData = "test"

        // When
        val resource = Resource.Success(testData)

        // Then
        assertEquals(testData, resource.data)
        assertEquals(null, resource.message)
        assertEquals(-1, resource.status)
    }

    @Test
    fun `Resource Error는 메시지와 상태를 포함해야 한다`() {
        // Given
        val errorMessage = "Error occurred"
        val statusCode = 500

        // When
        val resource = Resource.Error<String>(errorMessage, status = statusCode)

        // Then
        assertEquals(errorMessage, resource.message)
        assertEquals(statusCode, resource.status)
        assertEquals(null, resource.data)
    }

    @Test
    fun `Resource Loading은 선택적 데이터를 포함할 수 있어야 한다`() {
        // Given
        val cachedData = "cached"

        // When
        val resourceWithData = Resource.Loading(cachedData)
        val resourceWithoutData = Resource.Loading<String>()

        // Then
        assertEquals(cachedData, resourceWithData.data)
        assertEquals(null, resourceWithoutData.data)
    }
}

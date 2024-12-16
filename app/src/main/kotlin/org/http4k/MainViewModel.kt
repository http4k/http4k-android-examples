package org.http4k

import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.format.Jackson.json


private data class UUIDResponse(val uuid: String)

private val mapper = ObjectMapper().registerKotlinModule()


class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow("Hello World")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun callUsingFuel() {
        _uiState.update { "Making call" }

        Fuel.get(uuidEndpoint)
            .responseObject<UUIDResponse>(mapper) { _, _, result ->
                result.map { uuidResponse: UUIDResponse ->
                    _uiState.update { uuidResponse.uuid }
                }
            }
    }

    fun callUsingHttp4k() {
        _uiState.update { "Making call" }

        val client = AndroidClient(OkHttp())

        client(Request(GET, uuidEndpoint)) { result: UUIDResponse ->
            _uiState.update { result.uuid }
        }

//        Another option:
//
//        val result: UUIDResponse = client(Request(GET, uuidEndpoint)).json()
//        _uiState.update { result.uuid }
    }

    companion object {
        private const val uuidEndpoint = "https://httpbin.org/uuid"
    }
}

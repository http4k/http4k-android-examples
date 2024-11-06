package org.http4k.http4k_android_examples

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

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow("Hello World")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun callUsingFuel() {
        _uiState.update { "Making call" }
        Fuel.get("https://httpbin.org/uuid")
            .responseObject<UUIDResponse>(mapper) { _, _, result ->
                result.map { uuidResponse: UUIDResponse ->
                    _uiState.update { uuidResponse.uuid }
                }
            }
    }
}

data class UUIDResponse(val uuid: String)

val mapper = ObjectMapper().registerKotlinModule()
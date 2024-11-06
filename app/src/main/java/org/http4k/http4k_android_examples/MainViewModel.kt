package org.http4k.http4k_android_examples

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.filter.DebuggingFilters
import org.http4k.format.ConfigurableJackson
import org.http4k.http4k_android_examples.Http4kJackson.auto


private data class UUIDResponse(val uuid: String)

private val mapper = ObjectMapper().registerKotlinModule()

private object Http4kJackson : ConfigurableJackson(mapper)

private val lens = Body.auto<UUIDResponse>().toLens()

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

        val client = Filter.NoOp
            .then(DebuggingFilters.PrintRequestAndResponse())
            .then(OkHttp())

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                val response = client(Request(GET, uuidEndpoint))
                lens(response)
            }
            _uiState.update { result.uuid }
        }
    }

    companion object {
        private const val uuidEndpoint = "https://httpbin.org/uuid"
    }
}



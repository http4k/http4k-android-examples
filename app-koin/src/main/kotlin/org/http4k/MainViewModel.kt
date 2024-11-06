package org.http4k

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
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.format.ConfigurableJackson
import org.http4k.format.Jackson


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

        val client = OkHttp()

        viewModelScope.launch {
            val result: UUIDResponse = withContext(Dispatchers.IO) { client(Request(GET, uuidEndpoint)).body() }
            _uiState.update { result.uuid }
        }
    }

    companion object {
        private const val uuidEndpoint = "https://httpbin.org/uuid"
    }
}

inline fun <reified T : Any> HttpMessage.body(autoMarshalling: ConfigurableJackson = Jackson) =
    autoMarshalling.autoBody<T>().toLens()(this)



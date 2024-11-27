package org.http4k

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.http4k.core.HttpHandler
import org.http4k.core.HttpMessage
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.format.ConfigurableJackson
import org.http4k.format.Jackson
import org.http4k.format.Jackson.json


private data class UUIDResponse(val uuid: String)

class MainViewModel(private val client: HttpHandler) : ViewModel() {

    private val _uiState = MutableStateFlow("Hello World")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun callUsingHttp4k() {
        _uiState.update { "Making call" }

        viewModelScope.launch {
            val result: UUIDResponse = withContext(IO) { client(Request(GET, uuidEndpoint)).json() }
            _uiState.update { result.uuid }
        }
    }

    companion object {
        private const val uuidEndpoint = "https://httpbin.org/uuid"
    }
}



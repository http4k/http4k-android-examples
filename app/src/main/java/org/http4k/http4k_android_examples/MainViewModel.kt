package org.http4k.http4k_android_examples

import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow("Hello World")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    fun updateState() {

        _uiState.update { "Making call" }

        Fuel.get("https://httpbin.org/uuid")
            .response { _, _, result ->
                val (bytes, error) = result
                if (bytes != null) {
                    println("[response bytes] ${String(bytes)}")
                    _uiState.update { String(result.get()) }

                }
                if (error != null) {
                    println("[response error] $error")
                }
            }
    }
}
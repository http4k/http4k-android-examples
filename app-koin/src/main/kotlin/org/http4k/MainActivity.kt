package org.http4k

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import theme.Http4kandroidexamplesTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(mainModule)
        }

        enableEdgeToEdge()
        setContent {
            Http4kandroidexamplesTheme {
                MainView(viewModel = viewModel)
            }
        }
    }
}

val mainModule = module {
    single { OkHttp() } withOptions { bind<HttpHandler>() }
    factory { MainViewModel(get()) }
}
package com.brewkery.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.brewkery.app.navigation.AppNavigation
import com.brewkery.app.ui.theme.BrewkeryTheme
import com.brewkery.app.ui.theme.Cream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewkeryTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
                    AppNavigation()
                }
            }
        }
    }
}

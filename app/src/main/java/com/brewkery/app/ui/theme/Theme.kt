package com.brewkery.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = SurfaceWhite,
    secondary = Mocha,
    background = Cream,
    onBackground = Espresso,
    surface = SurfaceWhite,
    onSurface = Espresso,
    outline = BorderWarm
)

@Composable
fun BrewkeryTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = Typography, content = content)
}

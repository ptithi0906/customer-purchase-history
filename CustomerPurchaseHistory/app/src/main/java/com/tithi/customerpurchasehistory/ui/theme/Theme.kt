package com.tithi.customerpurchasehistory.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ShoppingLightColorScheme = lightColorScheme(
    primary = DarkBluePrimary,
    onPrimary = PureWhite,
    primaryContainer = LightBlueContainer,
    onPrimaryContainer = DarkBluePrimary,
    secondary = RoyalBlueAccent,
    onSecondary = PureWhite,
    secondaryContainer = LightBlueSurface,
    onSecondaryContainer = DarkBlueSecondary,
    background = OffWhiteBackground,
    onBackground = DarkBluePrimary,
    surface = PureWhite,
    onSurface = DarkBluePrimary,
    surfaceVariant = LightBlueSurface,
    onSurfaceVariant = SlateText,
    outline = BorderBlue,
    error = ErrorCrimson,
    onError = PureWhite
)

@Composable
fun CustomerPurchaseHistoryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShoppingLightColorScheme,
        content = content
    )
}

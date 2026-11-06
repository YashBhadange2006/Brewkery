# Brewkery

Brewkery is an Android coffee and bakery ordering app. Browse the menu, customize drinks and bakery items, add them to your cart, and place a local demo order.

## Features

- Browse menu items loaded from the Brewkery menu API.
- Search products by name or tagline.
- Filter the menu by category.
- View product details and choose available size, milk or spread, and sugar options.
- See customization prices update before adding an item to the cart.
- Manage quantities and review subtotal, delivery fee, tax, and total.
- Place a local demo order and view its ticket details.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose and Material 3 |
| Networking | Retrofit and Gson |
| Async state | Kotlin Coroutines and StateFlow |
| Navigation | Navigation Compose |
| Images | Coil |
| Unit tests | JUnit |

## Requirements

- Android Studio with a compatible Android SDK.
- Android 7.0 (API 24) or higher.
- Internet access to load the menu and product images.

## Build and Test

Clone the repository and open it in Android Studio:

```powershell
git clone https://github.com/YashBhadange2006/Brewkery.git
cd Brewkery
```

You can build the debug app and run the unit tests from PowerShell:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

## AI Assistance

AI was used to help investigate and fix the menu search behavior.

### Prompt used

> Fix issues in search and filter functionality

### Bug caught and fixed

The search field accepted text, but the displayed menu could remain unchanged. Filtered results now update from the current search query and menu state, and the list returns to the top when the query changes.

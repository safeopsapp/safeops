package com.safeops.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.safeops.app.core.designsystem.theme.SafeOpsTheme
import com.safeops.app.core.navigation.SafeOpsNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as SafeOpsApp).container

        setContent {
            SafeOpsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SafeOpsTheme.colors.background
                ) {
                    SafeOpsNavHost(container = appContainer)
                }
            }
        }
    }
}
package com.happyplaces.presentation.ui.compose.onboarding

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeStepScreen(
    vm: OnboardingViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    ),
    onFinish: () -> Unit
) = WelcomeStepContent(
    username = vm.uiState.username,
    onGo = {
        vm.onFinish()
        onFinish()
    }
)

/* Content */
@Composable
fun WelcomeStepContent(
    username: String,
    onGo: () -> Unit
) {
    Scaffold { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))
            Text(
                "Welcome to happy place ${username.ifBlank { "" }}!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "We’re now going to personalize your experience.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onGo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { Text("Let’s go") }
        }
    }
}

/* Preview */
@Preview(showBackground = true)
@Composable
fun WelcomeStepContentPreview() {
    HappyPlacesTheme {
        WelcomeStepContent(username = "Lin_Li", onGo = {})
    }
}
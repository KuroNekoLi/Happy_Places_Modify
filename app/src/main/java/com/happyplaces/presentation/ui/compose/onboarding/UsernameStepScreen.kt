package com.happyplaces.presentation.ui.compose.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UsernameStepScreen(
    vm: OnboardingViewModel = koinViewModel(),
    onNext: () -> Unit
) = UsernameStepContent(
    state = vm.uiState,
    onUsernameChange = vm::onUsernameChange,
    onNext = onNext
)

@Composable
fun UsernameStepContent(
    state: OnboardingViewModel.UiState,
    onUsernameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        step = 2, total = 4,
        title = "Choose a username",
        enableNext = state.usernameValid,
        onNext = onNext
    ) {
        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text("@username") },
            prefix = { Text("@") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            if (state.username.length >= 4) "Username available ✓" else "至少 4 個字元",
            color = if (state.username.length >= 4)
                MaterialTheme.colorScheme.primary
            else LocalContentColor.current.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/* Preview */
@Preview(showBackground = true)
@Composable
fun UsernameStepContentPreview() {
    HappyPlacesTheme {
        UsernameStepContent(
            state = OnboardingViewModel.UiState(username = "Lin_Li"),
            onUsernameChange = {},
            onNext = {}
        )
    }
}
package com.happyplaces.presentation.ui.compose.onboarding

import androidx.compose.foundation.layout.fillMaxWidth
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
fun EmailStepScreen(
    vm: OnboardingViewModel = koinViewModel(),   // 或 hiltViewModel()
    onNext: () -> Unit
) = EmailStepContent(
    uiState = vm.uiState,
    onEmailChange = vm::onEmailChange,
    onNext = onNext
)

@Composable
fun EmailStepContent(
    uiState: OnboardingViewModel.UiState,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        step = 1, total = 4,
        title = "Enter your email address",
        enableNext = uiState.emailValid,
        onNext = onNext
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("E-mail") },
            isError = uiState.email.isNotBlank() && !uiState.emailValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (uiState.email.isNotBlank() && !uiState.emailValid) {
            Text(
                "格式有誤，請再次確認",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmailStepContentPreview() {
    HappyPlacesTheme {
        EmailStepContent(
            uiState = OnboardingViewModel.UiState(
                email = "lin_li@cmoney.com.tw",
                emailValid = true
            ),
            onEmailChange = {},
            onNext = {}
        )
    }
}
package com.happyplaces.presentation.ui.compose.onboarding

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.happyplaces.R
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmailStepScreen(
    vm: OnboardingViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    ),
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
        title = stringResource(R.string.onboarding_email_title),
        enableNext = uiState.emailIsValid,
        onNext = onNext
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.onboarding_email_hint)) },
            isError = uiState.email.isNotBlank() && !uiState.emailIsValid,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (uiState.email.isNotBlank() && !uiState.emailIsValid) {
            Text(
                stringResource(R.string.onboarding_email_error),
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
                email = "lin_li@cmoney.com.tw"
            ),
            onEmailChange = {},
            onNext = {}
        )
    }
}

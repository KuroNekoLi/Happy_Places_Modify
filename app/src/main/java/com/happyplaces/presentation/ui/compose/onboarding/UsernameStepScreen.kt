package com.happyplaces.presentation.ui.compose.onboarding

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
    vm: OnboardingViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    ),
    onNext: () -> Unit
) = UsernameStepContent(
    state = vm.uiState,
    onUsernameChange = vm::onAccountIdChange,
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
        enableNext = state.accountIdValid && !state.isCheckingAccountId,
        onNext = onNext
    ) {
        OutlinedTextField(
            value = state.accountId,
            onValueChange = onUsernameChange,
            label = { Text("@username") },
            prefix = { Text("@") },
            singleLine = true,
            isError = state.accountIdError != null,
            modifier = Modifier.fillMaxWidth()
        )

        // 狀態顯示
        when {
            state.isCheckingAccountId -> {
                Text(
                    "檢查帳號中...",
                    color = LocalContentColor.current.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            state.accountIdError != null -> {
                Text(
                    state.accountIdError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            state.accountId.length >= 4 && state.accountIdError == null -> {
                Text(
                    "帳號可以使用 ✓",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            else -> {
                Text(
                    "至少 4 個字元",
                    color = LocalContentColor.current.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/* Preview */
@Preview(showBackground = true)
@Composable
fun UsernameStepContentPreview() {
    HappyPlacesTheme {
        UsernameStepContent(
            state = OnboardingViewModel.UiState(accountId = "Lin_Li"),
            onUsernameChange = {},
            onNext = {}
        )
    }
}

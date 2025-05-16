package com.happyplaces.presentation.ui.compose.onboarding

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileStepScreen(
    vm: OnboardingViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    ),
    onNext: () -> Unit
) {
    val state = vm.uiState

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(vm::onAvatarPicked) }

    ProfileStepContent(
        state = state,
        onPickAvatar = { pickMedia.launch(PickVisualMediaRequest(ImageOnly)) },
        onBioChange = vm::onBioChange,
        onNext = onNext
    )
}

/* ---------- 2) Stateless UI ---------- */
@Composable
fun ProfileStepContent(
    state: OnboardingViewModel.UiState,
    onPickAvatar: () -> Unit,
    onBioChange: (String) -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        step = 3, total = 4,
        title = "Complete your profile",
        enableNext = state.avatarUri != null,
        onNext = onNext
    ) {
        // ① 頭貼區
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(120.dp)) {
            if (state.avatarUri != null) {
                AsyncImage(
                    model = state.avatarUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    Icons.Default.Person, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), CircleShape)
                        .padding(24.dp)
                )
            }
            IconButton(
                onClick = onPickAvatar,
                modifier = Modifier
                    .offset(8.dp, 8.dp)
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ② Bio
        OutlinedTextField(
            value = state.bio,
            onValueChange = onBioChange,
            label = { Text("Write your bio") },
            placeholder = { Text("Tell us about yourself ...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 4
        )
    }
}

/* ---------- 3) Preview（純 UI 即可） ---------- */
@Preview(showBackground = true)
@Composable
fun ProfileStepContentPreview() {
    HappyPlacesTheme {
        ProfileStepContent(
            state = OnboardingViewModel.UiState(
                avatarUri = null,
                bio = ""
            ),
            onPickAvatar = {},
            onBioChange = {},
            onNext = {}
        )
    }
}
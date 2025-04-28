package com.happyplaces.presentation.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HappyPlaceToolBar(hasBack: Boolean, toolbarTitle: String, onBack: () -> Unit = {}) {
    TopAppBar(
        title = { Text(toolbarTitle) },
        navigationIcon = {
            if (hasBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HappyPlaceToolBarPreview(
    @PreviewParameter(BooleanPreviewProvider::class) hasBack: Boolean
) {
    HappyPlacesTheme {
        HappyPlaceToolBar(
            hasBack = hasBack,
            toolbarTitle = "Add Happy Place",
            onBack = { /* noop */ }
        )
    }
}

class BooleanPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(false, true)
}
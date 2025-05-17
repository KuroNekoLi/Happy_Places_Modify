package com.happyplaces.presentation.ui.compose.profile

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.happyplaces.BuildConfig
import com.happyplaces.R
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    versionName: String = BuildConfig.VERSION_NAME,
    privacyPolicyUrl: String = "https://www.google.com.tw",
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.primary.toArgb()
    // 準備 Custom Tabs
    val customTabsIntent = remember {
        CustomTabsIntent.Builder()
            .setToolbarColor(color)
            .build()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.settings)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            // 版本號
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.version)) },
                supportingContent = { Text(versionName) }
            )
            Divider()

            // 隱私權條款：點擊後開啟 Custom Tabs
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.privacy_policy)) },
                modifier = Modifier
                    .clickable {
                        customTabsIntent.launchUrl(
                            context,
                            privacyPolicyUrl.toUri()
                        )
                    }
            )
            Divider()

            // 登出
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.logout)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.logout)
                    )
                },
                modifier = Modifier
                    .clickable { onLogout() }
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    HappyPlacesTheme {
        SettingsScreen(
            versionName = "1.0.0",
            privacyPolicyUrl = "https://www.google.com.tw",
            onLogout = {}
        )
    }
}
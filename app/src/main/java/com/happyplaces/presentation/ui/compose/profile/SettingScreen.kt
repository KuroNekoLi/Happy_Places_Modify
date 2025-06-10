package com.happyplaces.presentation.ui.compose.profile

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.happyplaces.BuildConfig
import com.happyplaces.R
import com.happyplaces.data.Constant.PRIVACY_POLICY_URL
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.util.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    versionName: String = BuildConfig.VERSION_NAME,
    privacyPolicyUrl: String = PRIVACY_POLICY_URL,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.primary.toArgb()

    // 語言相關狀態
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(LanguageManager.getSavedLanguage(context)) }
    
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
            // 語言設定
            ListItem(
                headlineContent = { Text(text = stringResource(R.string.language)) },
                supportingContent = { Text(text = getLanguageDisplayName(currentLanguage)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = stringResource(R.string.language)
                    )
                },
                modifier = Modifier.clickable { showLanguageDialog = true }
            )
            Divider()
            
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

    // 語言選擇對話框
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { selectedLanguage ->
                currentLanguage = selectedLanguage
                LanguageManager.applyLanguage(context, selectedLanguage)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

/**
 * 語言選擇對話框
 */
@Composable
private fun LanguageSelectionDialog(
    currentLanguage: LanguageManager.Language,
    onLanguageSelected: (LanguageManager.Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.language)) },
        text = {
            Column {
                LanguageManager.Language.values().forEach { language ->
                    ListItem(
                        headlineContent = { Text(text = getLanguageDisplayName(language)) },
                        leadingContent = {
                            RadioButton(
                                selected = currentLanguage == language,
                                onClick = { onLanguageSelected(language) }
                            )
                        },
                        modifier = Modifier.clickable { onLanguageSelected(language) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 取得語言顯示名稱
 */
@Composable
private fun getLanguageDisplayName(language: LanguageManager.Language): String {
    return when (language) {
        LanguageManager.Language.SYSTEM -> stringResource(R.string.language_system)
        LanguageManager.Language.TRADITIONAL_CHINESE -> stringResource(R.string.language_chinese)
        LanguageManager.Language.ENGLISH -> stringResource(R.string.language_english)
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

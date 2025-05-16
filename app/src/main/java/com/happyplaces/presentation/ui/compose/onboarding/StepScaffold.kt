package com.happyplaces.presentation.ui.compose.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun StepScaffold(
    step: Int,
    total: Int,
    title: String,
    enableNext: Boolean,
    onNext: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        bottomBar = {
            Button(
                onClick = onNext,
                enabled = enableNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .imePadding()
            ) { Text("Next") }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 黃色進度條
            LinearProgressIndicator(
                progress = step / total.toFloat(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            Spacer(Modifier.height(32.dp))
            Text(style = MaterialTheme.typography.headlineSmall, text = title)
            Spacer(Modifier.height(24.dp))
            content()
        }
    }
}
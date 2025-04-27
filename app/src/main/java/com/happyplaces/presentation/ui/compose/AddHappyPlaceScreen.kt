package com.happyplaces.presentation.ui.compose

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.happyplaces.R
import lin.example.myapplication.ui.theme.HappyPlacesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHappyPlaceScreen(
    toolbarTitle: String,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    date: String,
    buttonText: String,
    onDateClick: () -> Unit,
    location: String,
    onLocationClick: () -> Unit,
    onSelectCurrentLocation: () -> Unit,
    imageUri: Uri?,
    onAddImageClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit,
) {
    val textFieldColor = TextFieldDefaults.colors().copy(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,          // 文字
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,   // 標籤
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,   // Placeholder
        disabledIndicatorColor = MaterialTheme.colorScheme.outline,            // 外框線
        disabledContainerColor = Color.Transparent                             // 背景
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(toolbarTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),         // main_content_padding
            verticalArrangement = Arrangement.spacedBy(16.dp)  // add_screen_til_marginTop
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.edit_text_hint_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.edit_text_hint_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Date (readOnly + 點擊彈出 DatePicker)
            OutlinedTextField(
                value = if (date.isBlank()) "Date" else date,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDateClick()
                    },
                colors = textFieldColor
            )

            // Location (readOnly + 點擊彈出地圖/選擇)
            OutlinedTextField(
                value = location,
                onValueChange = { /* no-op */ },
                enabled = false,
                label = { Text(stringResource(R.string.edit_text_hint_location)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLocationClick() },
                colors = textFieldColor
            )

            // 選取目前位置
            Text(
                text = stringResource(R.string.add_place_select_current_location_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(8.dp),            // shape_image_view_border
                        color = Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelectCurrentLocation() }
                    .padding(12.dp)                                  // add_place_select_current_location_padding
            )

            // 圖片 & 加圖按鈕
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)                               // add_screen_place_image_size
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onAddImageClick() }
                        .padding(8.dp),                              // add_screen_place_image_padding
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(id = R.drawable.add_screen_image_placeholder),
                    )
                }

                Text(
                    text = stringResource(R.string.text_add_image),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onAddImageClick() }
                        .padding(8.dp)                              // add_screen_text_add_image_padding
                )
            }

            // Save 按鈕
            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(16.dp),                 // shape_button_rounded
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddHappyPlaceScreenPreview() {
    HappyPlacesTheme {
        AddHappyPlaceScreen(
            title = "title",
            onTitleChange = {},
            description = "description",
            onDescriptionChange = {},
            date = "date",
            onDateClick = {},
            location = "location",
            onLocationClick = {},
            onSelectCurrentLocation = {},
            imageUri = null,
            onAddImageClick = {},
            onSaveClick = {},
            onBack = {},
            toolbarTitle = "Add Happy Place",
            buttonText = stringResource(R.string.btn_text_save)
        )
    }
}
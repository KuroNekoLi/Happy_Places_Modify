package com.happyplaces.presentation.ui.compose.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyplaces.R
import com.happyplaces.presentation.ui.compose.common.Avatar
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String = "John Doe",
    accountName: String = "john.doe@example.com",
    introduction: String = "John Doe is a software engineer",
    avatarUrl: String = "https://picsum.photos/200/300",
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton({}) {
                Icon(
                    painter = painterResource(id = R.drawable.add_friend),
                    contentDescription = null
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = accountName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal
                )
            }
            Avatar(imageUrl = "", size = 100.dp)
        }
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            text = introduction,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {}
            ) {
                Text(text = "Edit My Profile")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {}
            ) {
                Text(text = "Edit My Map")

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileScreen() {
    HappyPlacesTheme {
        ProfileScreen(
            username = "John Doe",
            introduction = "這是個非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常長的文字",
            avatarUrl = "",
            onSettingsClick = {}
        )
    }
}


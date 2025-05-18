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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyplaces.R
import com.happyplaces.data.model.mockHappyPlaceLists
import com.happyplaces.domain.model.HappyPlace
import com.happyplaces.presentation.ui.compose.common.Avatar
import com.happyplaces.presentation.ui.compose.common.happyPlaceItems
import com.happyplaces.presentation.ui.theme.HappyPlacesTheme
import com.happyplaces.presentation.ui.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    onSettingsClick: () -> Unit
) {
    val list by viewModel.myPlaces.collectAsState()
    val apiResource by viewModel.user.collectAsState()
    val user = apiResource?.data
    list.data?.let {
        ProfileScreen(
            modifier = modifier,
            username = user?.name.orEmpty(),
            accountName = user?.accountID.orEmpty(),
            introduction = user?.bio.orEmpty(),
            avatarUrl = user?.avatarUrl.orEmpty(),
            list = it
        ) {
            onSettingsClick()
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    accountName: String,
    introduction: String,
    avatarUrl: String,
    list: List<HappyPlace>,
    onItemClick: (HappyPlace) -> Unit = {},
    onSettingsClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
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
                        text = if (username.isBlank()) accountName else username,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = accountName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal
                    )
                }
                Avatar(imageUrl = avatarUrl, size = 100.dp)
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
        happyPlaceItems(
            list = list,
            isItemSwipeEnabled = true,
            onItemClick = onItemClick
        )
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
            list = mockHappyPlaceLists,
            onSettingsClick = {},
            accountName = "@1234",
        )
    }
}


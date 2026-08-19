package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminStudioScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DramaViewModel

enum class MainTab(val title: String, val icon: ImageVector, val tag: String) {
    FEED("Drama", Icons.Default.PlayCircle, "tab_feed"),
    EXPLORE("Terokai", Icons.Default.Explore, "tab_explore"),
    WALLET("Dompet", Icons.Default.AccountBalanceWallet, "tab_wallet"),
    ADMIN("Studio", Icons.Default.AdminPanelSettings, "tab_admin"),
    PROFILE("Profil", Icons.Default.Person, "tab_profile")
}

class MainActivity : ComponentActivity() {
    private val viewModel: DramaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: DramaViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(MainTab.FEED) }
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    val visibleTabs = if (isAdminMode) {
        listOf(MainTab.FEED, MainTab.EXPLORE, MainTab.WALLET, MainTab.ADMIN, MainTab.PROFILE)
    } else {
        listOf(MainTab.FEED, MainTab.EXPLORE, MainTab.WALLET, MainTab.PROFILE)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkCanvas,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xF0080808),
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.08f)
                    )
            ) {
                visibleTabs.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OrangePrimary,
                            selectedTextColor = OrangePrimary,
                            unselectedIconColor = Color.White.copy(alpha = 0.4f),
                            unselectedTextColor = Color.White.copy(alpha = 0.4f),
                            indicatorColor = OrangePrimary.copy(alpha = 0.18f)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.FEED -> FeedScreen(
                    viewModel = viewModel,
                    onNavigateToExplore = { currentTab = MainTab.EXPLORE },
                    onNavigateToWallet = { currentTab = MainTab.WALLET }
                )
                MainTab.EXPLORE -> ExploreScreen(
                    viewModel = viewModel,
                    onDramaSelected = { currentTab = MainTab.FEED }
                )
                MainTab.WALLET -> WalletScreen(
                    viewModel = viewModel
                )
                MainTab.ADMIN -> AdminStudioScreen(
                    viewModel = viewModel
                )
                MainTab.PROFILE -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToWallet = { currentTab = MainTab.WALLET }
                )
            }
        }
    }
}

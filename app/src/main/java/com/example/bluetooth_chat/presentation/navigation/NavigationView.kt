import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bluetooth_chat.presentation.add_user.AddUserView
import com.example.bluetooth_chat.presentation.chat.ChatView
import com.example.bluetooth_chat.presentation.contacts.ContactsView
import com.example.bluetooth_chat.presentation.navigation.NavigationViewModel
import com.example.bluetooth_chat.presentation.profile.ProfileView
import com.example.bluetooth_chat.presentation.scan.ScanView
import kotlin.collections.listOf


sealed class BottomNavItem(val route: String, val label: String) {
    object Contacts : BottomNavItem("contacts", "Contacts")
    object Profile : BottomNavItem("profile", "Profile")
    //object Settings : BottomNavItem("settings", "Settings")
}

val bottomNavItems = listOf(
    BottomNavItem.Contacts,
    BottomNavItem.Profile,
    //BottomNavItem.Settings
)
@Composable
fun NavigationView(
    navController: NavHostController = rememberNavController(),
    viewModel: NavigationViewModel = hiltViewModel<NavigationViewModel>(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.advertiseAccepted) {
        if(uiState.advertisingDevice != null && uiState.advertiseAccepted) {
            viewModel.resetAlert();
            navController.navigate("add_user/${uiState.advertisingDevice!!.address}/${uiState.advertisingDevice!!.name}")
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        modifier = modifier
    ) { paddingValues ->

        if(uiState.advertisingDevice != null) {
            AlertDialog(
                onDismissRequest = {
                        viewModel.alertDismiss()
                        viewModel.resetAlert()
                    },
                title = { Text("alert") },
                text = { Text("${uiState.advertisingDevice!!.name} wants to pair with you") },
                confirmButton = {
                    TextButton(onClick = { viewModel.alertConfirm() }) {
                        Text("CONFIRM")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.alertDismiss() }) {
                        Text("DISMISS")
                    }
                },
                properties = DialogProperties(dismissOnClickOutside = true )
            )
        }

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Contacts.route,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            composable("scan") {
                ScanView(
                    navController = navController,
                    modifier = Modifier.safeContentPadding()
                )
            }

            composable(BottomNavItem.Contacts.route) {
                ContactsView(
                    navController = navController,
                    modifier = Modifier.safeContentPadding()
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileView(
                    navController = navController,
                    modifier = Modifier.safeContentPadding(),
                )
            }

            composable("chat/{id}",
                arguments = listOf(
                    navArgument("id" ) {
                        type = NavType.IntType
                    }
                )
            ) { entry ->

                val id = entry.arguments?.getInt("id")
                if(id == null) {
                    navController.popBackStack()
                }

                ChatView(
                    contactId = id!!,
                    navController = navController,
                    modifier = Modifier.safeContentPadding()
                )
            }

            composable("add_user/{address}/{name}",
                arguments = listOf(
                    navArgument("address" ) {
                        type = NavType.StringType
                    },
                    navArgument("name" ) {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val address = entry.arguments?.getString("address")
                val name = entry.arguments?.getString("name")
                if(address == null || name == null) {
                    navController.popBackStack()
                }

                AddUserView(
                    deviceName = name!!,
                    deviceAddress = address!!,
                    navController = navController,
                    modifier = Modifier.safeContentPadding()
                )
            }

            //composable(BottomNavItem.Settings.route) {
            //    Text("not implemented", modifier = Modifier.safeContentPadding())
            //}
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId)
                    }
                },
                label = { Text(item.label) },
                icon = {}
            )
        }
    }
}
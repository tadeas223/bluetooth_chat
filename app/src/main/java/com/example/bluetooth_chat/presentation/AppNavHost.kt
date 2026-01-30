import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bluetooth_chat.presentation.contacts.ContactsView
import com.example.bluetooth_chat.presentation.profile.ProfileView
import com.example.bluetooth_chat.presentation.scan.ScanView


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
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Contacts.route,
            modifier = modifier
                .fillMaxSize()
        ) {

            composable("scan") {
                ScanView(
                    navController = navController,
                    modifier = modifier.safeContentPadding()
                )
            }

            composable(BottomNavItem.Contacts.route) {
                ContactsView(
                    navController = navController,
                    modifier = modifier.safeContentPadding()
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileView(
                    navController = navController,
                    modifier = modifier.safeContentPadding(),
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
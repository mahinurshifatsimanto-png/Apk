package com.mychat.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.mychat.app.ui.auth.ForgotPasswordScreen
import com.mychat.app.ui.auth.LoginScreen
import com.mychat.app.ui.auth.RegisterScreen
import com.mychat.app.ui.chat.ChatScreen
import com.mychat.app.ui.home.HomeScreen
import com.mychat.app.ui.profile.EditProfileScreen
import com.mychat.app.ui.profile.ProfileScreen
import com.mychat.app.ui.profile.UserProfileScreen
import com.mychat.app.ui.search.SearchUserScreen
import com.mychat.app.ui.settings.SettingsScreen
import com.mychat.app.ui.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController    = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.SearchUser.route) {
            SearchUserScreen(navController = navController)
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("receiverId") { type = NavType.StringType }
            )
        ) { backStack ->
            val chatId = backStack.arguments?.getString("chatId") ?: ""
            val receiverId = backStack.arguments?.getString("receiverId") ?: ""
            ChatScreen(
                navController = navController,
                chatId = chatId,
                receiverId = receiverId
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStack ->
            val userId = backStack.arguments?.getString("userId") ?: ""
            UserProfileScreen(navController = navController, userId = userId)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}

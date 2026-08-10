package com.mychat.app.navigation

sealed class Screen(val route: String) {

    object Splash        : Screen("splash")
    object Login         : Screen("login")
    object Register      : Screen("register")
    object ForgotPassword: Screen("forgot_password")
    object Home          : Screen("home")
    object SearchUser    : Screen("search_user")
    object Profile       : Screen("profile")
    object EditProfile   : Screen("edit_profile")
    object Settings      : Screen("settings")

    object Chat : Screen("chat/{chatId}/{receiverId}") {
        fun createRoute(chatId: String, receiverId: String) =
            "chat/$chatId/$receiverId"
    }

    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
}

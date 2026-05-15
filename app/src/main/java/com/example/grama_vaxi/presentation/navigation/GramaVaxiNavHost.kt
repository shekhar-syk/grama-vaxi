package com.example.grama_vaxi.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.grama_vaxi.domain.model.AppLanguage
import com.example.grama_vaxi.presentation.screens.auth.LoginScreen
import com.example.grama_vaxi.presentation.screens.auth.OnboardingLanguageScreen
import com.example.grama_vaxi.presentation.screens.auth.SplashScreen
import com.example.grama_vaxi.presentation.screens.farmer.AnimalDetailScreen
import com.example.grama_vaxi.presentation.screens.farmer.AnimalLedgerScreen
import com.example.grama_vaxi.presentation.screens.farmer.AppPermissionsScreen
import com.example.grama_vaxi.presentation.screens.farmer.CampAlertScreen
import com.example.grama_vaxi.presentation.screens.farmer.CampQrResultScreen
import com.example.grama_vaxi.presentation.screens.farmer.CampQrScannerScreen
import com.example.grama_vaxi.presentation.screens.farmer.DiseaseReportScreen
import com.example.grama_vaxi.presentation.screens.farmer.EditProfileScreen
import com.example.grama_vaxi.presentation.screens.farmer.HomeDashboardScreen
import com.example.grama_vaxi.presentation.screens.farmer.LanguageSettingsScreen
import com.example.grama_vaxi.presentation.screens.farmer.NotificationSettingsScreen
import com.example.grama_vaxi.presentation.screens.farmer.NotificationsScreen
import com.example.grama_vaxi.presentation.screens.farmer.ProfileScreen
import com.example.grama_vaxi.presentation.screens.farmer.RegisterAnimalScreen
import com.example.grama_vaxi.presentation.screens.farmer.TermsAndConditionsScreen
import com.example.grama_vaxi.presentation.screens.farmer.ThemeSettingsScreen
import com.example.grama_vaxi.presentation.screens.farmer.VaccineCalendarScreen
import com.example.grama_vaxi.presentation.viewmodel.AnimalDetailViewModel
import com.example.grama_vaxi.presentation.viewmodel.AnimalLedgerViewModel
import com.example.grama_vaxi.presentation.viewmodel.AuthViewModel
import com.example.grama_vaxi.presentation.viewmodel.CampAlertViewModel
import com.example.grama_vaxi.presentation.viewmodel.DiseaseReportViewModel
import com.example.grama_vaxi.presentation.viewmodel.HomeViewModel
import com.example.grama_vaxi.presentation.viewmodel.NotificationsViewModel
import com.example.grama_vaxi.presentation.viewmodel.RegisterAnimalViewModel
import com.example.grama_vaxi.presentation.viewmodel.VaccineCalendarViewModel

@Composable
fun GramaVaxiNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val farmerRoutes = setOf(
        NavRoutes.FarmerHome,
        NavRoutes.AnimalLedger,
        NavRoutes.VaccineCalendar,
        NavRoutes.Notifications
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (currentRoute in farmerRoutes) {
                FarmerTopBar(
                    onProfileClick = { navController.navigate(NavRoutes.Profile) }
                )
            }
        },
        bottomBar = {
            if (currentRoute in farmerRoutes) {
                FarmerBottomBar(currentRoute = currentRoute) { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.Splash) {
                SplashScreen(
                    onFinished = {
                        val destination = if (authState.session.isLoggedIn) {
                            NavRoutes.FarmerHome
                        } else {
                            NavRoutes.Login
                        }
                        navController.navigate(destination) {
                            popUpTo(NavRoutes.Splash) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.Login) {
                val context = androidx.compose.ui.platform.LocalContext.current
                LoginScreen(
                    uiState = authState,
                    onPhoneChanged = authViewModel::onPhoneChanged,
                    onEmailChanged = authViewModel::onEmailChanged,
                    onOtpChanged = authViewModel::onOtpChanged,
                    onSendOtp = {
                        authViewModel.sendOtp()
                    },
                    onLogin = {
                        authViewModel.verifyOtp { isNewUser ->
                            val route = if (isNewUser) NavRoutes.OnboardingLanguage else NavRoutes.FarmerHome
                            navController.navigate(route) {
                                popUpTo(NavRoutes.Login) { inclusive = true }
                            }
                        }
                    },
                    onGoogleSignIn = {
                        authViewModel.signInWithGoogle(
                            context = context,
                            webClientId = context.getString(com.example.grama_vaxi.R.string.default_web_client_id)
                        ) { isNewUser ->
                            val route = if (isNewUser) NavRoutes.OnboardingLanguage else NavRoutes.FarmerHome
                            navController.navigate(route) {
                                popUpTo(NavRoutes.Login) { inclusive = true }
                            }
                        }
                    },
                    onToggleMethod = authViewModel::setLoginMethod
                )
            }

            composable(NavRoutes.OnboardingLanguage) {
                OnboardingLanguageScreen(
                    currentLanguage = authState.session.language,
                    onLanguageSelected = authViewModel::selectLanguage,
                    onNext = {
                        navController.navigate(NavRoutes.SignUp) {
                            popUpTo(NavRoutes.OnboardingLanguage) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavRoutes.SignUp) {
                com.example.grama_vaxi.presentation.screens.auth.SignUpScreen(
                    authViewModel = authViewModel,
                    onRegistrationComplete = {
                        navController.navigate(NavRoutes.FarmerHome) {
                            popUpTo(NavRoutes.SignUp) { inclusive = true }
                        }
                    }
                )
            }


            composable(NavRoutes.FarmerHome) {
                val viewModel: HomeViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(authState.session.userUid) {
                    if (authState.session.userUid.isNotBlank()) {
                        viewModel.start(authState.session.userUid)
                    }
                }

                HomeDashboardScreen(
                    uiState = uiState,
                    onRegisterAnimal = { navController.navigate(NavRoutes.RegisterAnimal) },
                    onViewCalendar = { navController.navigate(NavRoutes.VaccineCalendar) },
                    onReportDisease = { navController.navigate(NavRoutes.DiseaseReport) },
                    onOpenLedger = { navController.navigate(NavRoutes.AnimalLedger) },
                    onOpenAlerts = { navController.navigate(NavRoutes.Notifications) }
                )
            }

            composable(NavRoutes.AnimalLedger) {
                val viewModel: AnimalLedgerViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(authState.session.userUid) {
                    if (authState.session.userUid.isNotBlank()) {
                        viewModel.start(authState.session.userUid)
                    }
                }

                AnimalLedgerScreen(
                    uiState = uiState,
                    onAddAnimal = { navController.navigate(NavRoutes.RegisterAnimal) },
                    onDeleteAnimal = viewModel::deleteAnimal,
                    onOpenAnimal = { animalId -> navController.navigate(NavRoutes.animalDetail(animalId)) }
                )
            }

            composable(NavRoutes.RegisterAnimal) {
                val viewModel: RegisterAnimalViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                RegisterAnimalScreen(
                    uiState = uiState,
                    onNameChanged = viewModel::onNameChanged,
                    onBreedChanged = viewModel::onBreedChanged,
                    onVillageChanged = viewModel::onVillageChanged,
                    onAgeChanged = viewModel::onAgeChanged,
                    onTypeChanged = viewModel::onTypeChanged,
                    onPickPhoto = { viewModel.onPhotoChanged(null) },
                    onSubmit = {
                        viewModel.register(authState.session.userUid) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(NavRoutes.CampQrScanner) {
                CampQrScannerScreen(
                    ownerUid = authState.session.userUid,
                    onScanned = { payload ->
                        navController.navigate(NavRoutes.campQrResult(Uri.encode(payload))) {
                            popUpTo(NavRoutes.CampQrScanner) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.CampQrResultPattern,
                arguments = listOf(navArgument("payload") { type = NavType.StringType })
            ) { entry ->
                val payload = Uri.decode(entry.arguments?.getString("payload").orEmpty())
                CampQrResultScreen(
                    payload = payload,
                    onBack = { navController.popBackStack() },
                    onOpenCalendar = { navController.navigate(NavRoutes.VaccineCalendar) },
                    onOpenAlerts = { navController.navigate(NavRoutes.Notifications) }
                )
            }

            composable(NavRoutes.VaccineCalendar) {
                val viewModel: VaccineCalendarViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(authState.session.userUid) {
                    if (authState.session.userUid.isNotBlank()) {
                        viewModel.start(authState.session.userUid)
                    }
                }

                VaccineCalendarScreen(
                    uiState = uiState,
                    onScanCampQr = { navController.navigate(NavRoutes.CampQrScanner) },
                    onDateSelected = viewModel::selectDate
                )
            }

            composable(NavRoutes.Notifications) {
                val viewModel: NotificationsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(authState.session.userUid) {
                    if (authState.session.userUid.isNotBlank()) {
                        viewModel.start(authState.session.userUid)
                    }
                }

                NotificationsScreen(
                    uiState = uiState,
                    onMarkRead = viewModel::markAsRead,
                    onOpenAlert = { alertId -> navController.navigate(NavRoutes.campAlert(alertId)) },
                    onOpenVaccine = { animalId -> navController.navigate(NavRoutes.animalDetail(animalId)) },
                    onScanCampQr = { navController.navigate(NavRoutes.CampQrScanner) }
                )
            }

            composable(NavRoutes.EditProfile) {
                EditProfileScreen(
                    uiState = authState,
                    onSaveProfile = authViewModel::updateProfile,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.ThemeSettings) {
                ThemeSettingsScreen(
                    currentTheme = authState.session.theme,
                    onThemeSelected = authViewModel::selectTheme,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.LanguageSettings) {
                LanguageSettingsScreen(
                    currentLanguage = authState.session.language,
                    onLanguageSelected = authViewModel::selectLanguage,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.NotificationSettings) {
                NotificationSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.AppPermissions) {
                AppPermissionsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.TermsConditions) {
                TermsAndConditionsScreen(
                    isDeleting = authState.isDeletingAccount,
                    onDeleteAccount = { onResult ->
                        authViewModel.deleteAccount { success, message ->
                            onResult(success, message)
                            if (success) {
                                navController.navigate(NavRoutes.Login) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.DiseaseReport) {
                val viewModel: DiseaseReportViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(authState.session.userUid) {
                    if (authState.session.userUid.isNotBlank()) {
                        viewModel.start(authState.session.userUid)
                    }
                }

                DiseaseReportScreen(
                    uiState = uiState,
                    onAnimalSelected = viewModel::onAnimalSelected,
                    onSymptomsChanged = viewModel::onSymptomsChanged,
                    onNotesChanged = viewModel::onNotesChanged,
                    onAffectedCountChanged = viewModel::onAffectedCountChanged,
                    onClassifySymptoms = viewModel::classifySymptoms,
                    onSubmit = {
                        viewModel.submit(authState.session.userUid) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(
                route = NavRoutes.CampAlertPattern,
                arguments = listOf(navArgument("alertId") { type = NavType.StringType })
            ) { entry ->
                val alertId = entry.arguments?.getString("alertId").orEmpty()
                val viewModel: CampAlertViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(alertId, authState.session.userUid) {
                    viewModel.load(authState.session.userUid, alertId)
                }

                CampAlertScreen(
                    alert = uiState.alert,
                    onMarkRead = viewModel::markRead
                )
            }

            composable(
                route = NavRoutes.AnimalDetailPattern,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { entry ->
                val animalId = entry.arguments?.getString("animalId").orEmpty()
                val viewModel: AnimalDetailViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(animalId, authState.session.userUid) {
                    viewModel.loadAnimal(authState.session.userUid, animalId)
                }

                AnimalDetailScreen(
                    uiState = uiState,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.Profile) {
                ProfileScreen(
                    uiState = authState,
                    onEditProfile = { navController.navigate(NavRoutes.EditProfile) },
                    onOpenNotifications = { navController.navigate(NavRoutes.Notifications) },
                    onOpenNotificationSettings = { navController.navigate(NavRoutes.NotificationSettings) },
                    onOpenAppPermissions = { navController.navigate(NavRoutes.AppPermissions) },
                    onOpenTerms = { navController.navigate(NavRoutes.TermsConditions) },
                    onOpenTheme = { navController.navigate(NavRoutes.ThemeSettings) },
                    onOpenLanguage = { navController.navigate(NavRoutes.LanguageSettings) },
                    onLogout = {
                        authViewModel.signOut()
                        navController.navigate(NavRoutes.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FarmerTopBar(onProfileClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Grama-Vaxi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

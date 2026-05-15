package com.example.grama_vaxi.presentation.navigation

object NavRoutes {
    const val Splash = "splash"
    const val Language = "language"
    const val OnboardingLanguage = "onboarding_language"
    const val Login = "login"
    const val Otp = "otp"
    const val SignUp = "sign_up"

    const val FarmerHome = "farmer_home"
    const val AnimalLedger = "animal_ledger"
    const val RegisterAnimal = "register_animal"
    const val VaccineCalendar = "vaccine_calendar"
    const val Notifications = "notifications"
    const val DiseaseReport = "disease_report"
    const val Profile = "profile"
    const val EditProfile = "edit_profile"
    const val ThemeSettings = "theme_settings"
    const val LanguageSettings = "language_settings"
    const val NotificationSettings = "notification_settings"
    const val AppPermissions = "app_permissions"
    const val TermsConditions = "terms_conditions"

    const val CampQrScanner = "camp_qr_scanner"

    const val CampQrResultPattern = "camp_qr_result/{payload}"
    fun campQrResult(payload: String): String = "camp_qr_result/$payload"

    const val CampAlertPattern = "camp_alert/{alertId}"
    fun campAlert(alertId: String): String = "camp_alert/$alertId"

    const val AnimalDetailPattern = "animal_detail/{animalId}"
    fun animalDetail(animalId: String): String = "animal_detail/$animalId"
}

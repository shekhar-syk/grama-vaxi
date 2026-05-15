<<<<<<< HEAD
# Grama-Vaxi Farmer Application

The Grama-Vaxi Farmer App is a specialized Android application designed for livestock owners in rural regions. It serves as a digital companion for managing animal health, tracking vaccination schedules, and maintaining direct communication with veterinary authorities.

---

## Core Features

### Animal Management
*   **Digital Registration**: Register livestock with unique identifiers, species (Cow, Goat, Sheep), breed, and age.
*   **Photo Documentation**: Store visual records of animals for easy identification and health monitoring.
*   **Health Ledger**: Track historical health events and previous vaccinations for each registered animal.

### Vaccination Tracking
*   **Automated Reminders**: Receive push notifications for upcoming vaccination dates.
*   **Status Indicators**: Visual cues (Green, Amber, Red) to indicate whether an animal's vaccinations are up to date, upcoming, or overdue.

### Emergency & Reporting
*   **Disease Reporting**: Submit rapid reports for suspected outbreaks, including symptoms and affected animal counts.
*   **Camp Alerts**: Receive instant notifications about regional vaccination camps organized by the veterinary department.

---

## Technical Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Modern, declarative UI)
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Data Layer**:
    *   **Remote**: Firebase Firestore (Real-time sync)
    *   **Auth**: Firebase Authentication (Phone-based login)
    *   **Push Notifications**: Firebase Cloud Messaging (FCM)
*   **Image Handling**: Coil / Firebase Storage

---

## Project Structure

*   `app/src/main/java/com/gramavaxi/`: Contains the core logic, viewmodels, and UI components.
*   `app/src/main/res/`: Contains application resources, icons, and layout definitions.
*   `docs/`: Detailed technical documentation including PRD, Architecture, and Navigation flows.

---

## Setup and Installation

1.  Clone the repository.
2.  Open the project in Android Studio (Ladybug or later).
3.  Ensure a valid `google-services.json` is placed in the `app/` directory.
4.  Build and run on an Android device or emulator (API 24+ recommended).

---

## Security

The application implements phone-based authentication to ensure that animal records and reporting are tied to verified farmer identities within the regional database.
=======
# grama-vaxi
>>>>>>> 838d978d4d6fe9e4b912b8c9f2727d8fa96edcf6

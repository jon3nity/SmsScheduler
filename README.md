# SMS Scheduler Android Application

**Automated SMS delivery system with precise scheduling, contact integration, and persistent storage built with Kotlin and Jetpack Compose.**

Undergraduate final year project demonstrating mobile application development, Android system integration, and modern UI design patterns.

---

## Overview

Android application enabling users to schedule SMS messages for automatic delivery at specified dates and times. Built with Kotlin, Jetpack Compose, and Material 3 design system, leveraging native Android APIs for reliable message scheduling.

**Platform:** Android 7.0+ (API 24+)  
**Architecture:** Clean Architecture with MVVM  
**Status:** Production Release (v1.0.0)

---

## Key Features

**Core Functionality:**
- Schedule SMS messages with exact date/time delivery
- Multiple recipient support (comma-separated or contact picker)
- Persistent storage survives app restarts and reboots
- AlarmManager integration for precise timing (works in sleep mode)
- Input validation prevents past-date scheduling

**User Experience:**
- Material 3 design with dark mode support
- Three-screen flow (Home, Schedule, View)
- Visual calendar and time pickers
- Responsive layout for multiple screen sizes

---

## Technology Stack

**Development:**
- Kotlin (primary language)
- Jetpack Compose (declarative UI)
- Material 3 (design system)
- Android Studio + Gradle

**Android Components:**
- AlarmManager (scheduling)
- SmsManager (delivery)
- BroadcastReceiver (event handling)
- SharedPreferences (storage)
- ContentProvider (contacts)

**Libraries:**
- Gson (JSON serialization)
- AndroidX Core KTX
- Compose BOM
- Activity Compose

---

## Architecture

Clean architecture with separation of concerns:

```
SMS Scheduler
├── UI Layer (Jetpack Compose)
│   └── Material 3 components
├── Data Layer (Repository pattern)
│   └── SharedPreferences + Gson
├── Domain Layer (Business logic)
│   └── Validation rules
└── System Integration
    ├── AlarmManager (scheduler)
    └── BroadcastReceiver (sender)
```

---

## Technical Implementation

**Scheduling Flow:**
1. User input validation (future date/time required)
2. Message serialized to JSON (Gson)
3. Stored in SharedPreferences with unique ID
4. AlarmManager registers exact alarm
5. BroadcastReceiver triggers at scheduled time
6. SmsManager sends to all recipients
7. Cleanup: schedule removed after delivery

**Data Persistence:**
- JSON serialization for structured storage
- Survives app restarts and device reboots
- Automatic cleanup post-delivery
- Efficient read/write operations

---

## Installation

**Prerequisites:**
- Android Studio Hedgehog or later
- Android SDK 24+
- Device/emulator with SMS capabilities

**Setup:**
```bash
git clone https://github.com/jon3nity/SmsScheduler.git
cd SmsScheduler
# Open in Android Studio
# Sync Gradle
# Run on device/emulator
```

---

## Usage

**Required Permissions:**
- SEND_SMS (core functionality)
- READ_CONTACTS (contact picker)
- SCHEDULE_EXACT_ALARM (precise timing)
- READ_PHONE_STATE (SMS operations)
- WAKE_LOCK (device wake)

**Schedule a Message:**
1. Tap "New Schedule"
2. Enter recipients (manual or contact picker)
3. Compose message
4. Select date and time
5. Confirm schedule

**Manage Schedules:**
- View all pending schedules
- Cancel individual messages
- Automatic cleanup after delivery

---

## Capabilities Demonstrated

**Mobile Development:**
- Modern Android with Kotlin
- Jetpack Compose declarative UI
- Material Design 3 implementation
- Responsive layout design

**System Integration:**
- Background scheduling (AlarmManager)
- Event-driven architecture (BroadcastReceiver)
- SMS API integration
- Contact provider integration

**Data Management:**
- Local persistent storage
- JSON serialization
- State management
- Input validation

---

## Known Limitations

**Current Implementation:**
- Local storage only (no cloud sync)
- Manual scheduling (no recurring)
- Single device operation
- Battery optimization may affect delivery on some devices

**Workarounds:**
- Disable battery optimization in settings
- Add to protected/autostart list
- Test with different carriers if restricted

---

## Future Enhancements

**Features:**
- Message templates
- Recurring message support
- Cloud backup
- Delivery confirmations
- Contact groups

**Technical:**
- Room Database integration
- ViewModel architecture
- Coroutines for async operations
- WorkManager for reliability
- Comprehensive testing

---

## Project Structure

```
SmsSchedulerApp/
├── app/src/main/java/
│   ├── MainActivity.kt          # UI + navigation
│   ├── MyBroadcastReceiver.kt   # SMS sender
│   ├── ScheduledMessage.kt      # Data model
│   └── ScheduleRepository.kt    # Storage manager
├── res/                         # Resources
└── AndroidManifest.xml          # Configuration
```

---

## Contributing

Academic project demonstrating mobile development capabilities.

**Contact:**
- LinkedIn: [john-onyekachi](https://www.linkedin.com/in/john-onyekachi)
- Email: jon3nity.oj@gmail.com
- GitHub: [@jon3nity](https://github.com/jon3nity)

---

## Acknowledgments

**Technologies:** Android Open Source Project, Jetpack Compose, Material Design, Kotlin

**Purpose:** Undergraduate final year project demonstrating Android development proficiency for software engineering portfolio.

---

**Version:** 1.0.0  
**Last Updated:** 2024

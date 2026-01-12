package com.example.smsschedulerapp

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.smsschedulerapp.ui.theme.SmsSchedulerAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Data class to hold contact information
data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String
)

class MainActivity : ComponentActivity() {
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.READ_CONTACTS
    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            Toast.makeText(this, "All required permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAppPermissions()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkExactAlarmPermission()
        }

        setContent {
            SmsSchedulerAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigator()
                }
            }
        }
    }

    private fun requestAppPermissions() {
        if (requiredPermissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            // All permissions already granted
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkExactAlarmPermission() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                this,
                "Exact alarm permission is required for scheduled SMS",
                Toast.LENGTH_LONG
            ).show()

            Intent().also { settingsIntent ->
                settingsIntent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                startActivity(settingsIntent)
            }
        }
    }
}

@Composable
fun AppNavigator() {
    var currentScreen by remember { mutableStateOf("Home") }

    when (currentScreen) {
        "Home" -> LandingPage(
            onSchedulesClick = { currentScreen = "ViewSchedules" },
            onNewScheduleClick = { currentScreen = "NewSchedule" }
        )
        "NewSchedule" -> SmsSchedulerApp(
            onBackPressed = { currentScreen = "Home" }
        )
        "ViewSchedules" -> SchedulesList(
            onBackPressed = { currentScreen = "Home" }
        )
    }
}

@Composable
fun LandingPage(onSchedulesClick: () -> Unit, onNewScheduleClick: () -> Unit) {
    val activity = LocalContext.current as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SMS Scheduler",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Manage your scheduled messages",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Menu options
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Schedules Button
                Button(
                    onClick = onSchedulesClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "View Schedules",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "  Schedules",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // New Schedule Button
                Button(
                    onClick = onNewScheduleClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Schedule",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "  New Schedule",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Push the exit button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Exit Button
        Button(
            onClick = { activity?.finish() },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Exit App"
            )
            Text(
                text = "  Exit",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Developed by Chinedu",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SchedulesList(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with back button
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onBackPressed,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("Back")
            }

            Text(
                text = "Scheduled Messages",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Placeholder for scheduled messages
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No scheduled messages yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDatePicker(
    label: String,
    initialDate: Long? = null,
    onDateSelected: (Long) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDate?.let { dateFormatter.format(Date(it)) } ?: "",
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Button(
                    onClick = { isDatePickerVisible = true }
                ) {
                    Text("Select")
                }
            }
        )

        if (isDatePickerVisible) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate
            )

            DatePickerDialog(
                onDismissRequest = { isDatePickerVisible = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = it
                                onDateSelected(it)
                            }
                            isDatePickerVisible = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isDatePickerVisible = false }
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    label: String,
    initialTime: String = "",
    onTimeSelected: (String) -> Unit
) {
    var selectedTime by remember { mutableStateOf(initialTime) }
    var isTimePickerVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedTime,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Button(
                    onClick = { isTimePickerVisible = true }
                ) {
                    Text("Select")
                }
            }
        )

        if (isTimePickerVisible) {
            val timePickerState = rememberTimePickerState()

            AlertDialog(
                onDismissRequest = { isTimePickerVisible = false },
                title = { Text("Select Time") },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(state = timePickerState)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                            selectedTime = formattedTime
                            onTimeSelected(formattedTime)
                            isTimePickerVisible = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isTimePickerVisible = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ContactPicker(onContactSelected: (Contact) -> Unit) {
    val context = LocalContext.current

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { uri: Uri? ->
            uri?.let {
                val contact = getContactFromUri(context, it)
                contact?.let { c -> onContactSelected(c) }
            }
        }
    )

    Button(
        onClick = { contactPickerLauncher.launch(null) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Select Contact"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add from Contacts")
    }
}

@Composable
fun ContactsList(contacts: List<Contact>, onRemoveContact: (Contact) -> Unit) {
    if (contacts.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Selected Contacts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(contacts) { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = contact.phoneNumber,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            IconButton(onClick = { onRemoveContact(contact) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Remove Contact"
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

fun getContactFromUri(context: Context, uri: Uri): Contact? {
    val contactInfo = extractContactInfo(context, uri)

    return if (contactInfo.first.isNotEmpty() &&
        contactInfo.second.isNotEmpty() &&
        contactInfo.third.isNotEmpty()) {
        Contact(contactInfo.first, contactInfo.second, contactInfo.third)
    } else {
        Toast.makeText(context, "Could not retrieve contact information", Toast.LENGTH_SHORT).show()
        null
    }
}

// Helper function to extract contact info that avoids smart cast issues
private fun extractContactInfo(context: Context, uri: Uri): Triple<String, String, String> {
    var contactId = ""
    var displayName = ""
    var phoneNumber = ""

    // Get contact ID and display name
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)

            if (idIndex >= 0) contactId = cursor.getString(idIndex) ?: ""
            if (nameIndex >= 0) displayName = cursor.getString(nameIndex) ?: ""
        }
    }

    // Get phone number
    if (contactId.isNotEmpty()) {
        val phoneCursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        phoneCursor?.use {
            if (it.moveToFirst()) {
                val phoneIndex = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (phoneIndex >= 0) phoneNumber = it.getString(phoneIndex) ?: ""
            }
        }
    }

    return Triple(contactId, displayName, phoneNumber)
}

@Composable
fun SmsSchedulerApp(onBackPressed: () -> Unit) {
    var phoneNumbers by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Store selected contacts
    val contacts = remember { mutableStateListOf<Contact>() }

    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar with back button
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onBackPressed,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("Back")
            }

            Text(
                text = "New Schedule",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumbers,
            onValueChange = { phoneNumbers = it },
            label = { Text("Phone Numbers (comma-separated)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        ContactPicker(onContactSelected = { contact ->
            // Add contact if not already in the list
            if (contacts.none { it.id == contact.id }) {
                contacts.add(contact)
            } else {
                Toast.makeText(context, "Contact already added", Toast.LENGTH_SHORT).show()
            }
        })

        // Display selected contacts
        ContactsList(
            contacts = contacts,
            onRemoveContact = { contact ->
                contacts.remove(contact)
            }
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        CalendarDatePicker(
            label = "Select Date",
            initialDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
            }
        )

        TimePicker(
            label = "Select Time",
            initialTime = selectedTime,
            onTimeSelected = { time ->
                selectedTime = time
            }
        )

        Button(
            onClick = {
                // Combine manually entered numbers and contact numbers
                val manualNumbersList = parsePhoneNumbers(phoneNumbers)
                val contactNumbersList = contacts.map { it.phoneNumber }
                val allNumbers = (manualNumbersList + contactNumbersList).distinct()

                if (allNumbers.isEmpty()) {
                    Toast.makeText(context, "Please add at least one phone number", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val dateTime = if (selectedDate != null && selectedTime.isNotEmpty()) {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = selectedDate!!

                    val timeParts = selectedTime.split(":")
                    if (timeParts.size == 2) {
                        calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                        calendar.set(Calendar.MINUTE, timeParts[1].toInt())
                        calendar.set(Calendar.SECOND, 0)
                    }

                    dateTimeFormatter.format(calendar.time)
                } else {
                    ""
                }

                scheduleSms(
                    phoneNumbers = allNumbers,
                    message = message,
                    dateTime = dateTime,
                    context = context
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule SMS")
        }
    }
}

fun scheduleSms(phoneNumbers: List<String>, message: String, dateTime: String, context: Context) {
    if (phoneNumbers.isEmpty() || message.isEmpty() || dateTime.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    try {
        val date = sdf.parse(dateTime)
        val timeInMillis = date?.time ?: return

        val currentTimeMillis = System.currentTimeMillis()
        Log.d("SMS_SCHEDULER", "Current time: ${Date(currentTimeMillis)}")
        Log.d("SMS_SCHEDULER", "Scheduled time: ${Date(timeInMillis)}")
        Log.d("SMS_SCHEDULER", "Time difference (minutes): ${(timeInMillis - currentTimeMillis) / (1000 * 60)}")

        if (timeInMillis <= currentTimeMillis) {
            Toast.makeText(context, "Please select a future time", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, MyBroadcastReceiver::class.java).apply {
            putExtra("phoneNumbers", ArrayList(phoneNumbers))
            putExtra("message", message)
            putExtra("scheduledTime", timeInMillis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            // Since minSdk is 24, we can always use setExactAndAllowWhileIdle
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )

            Toast.makeText(context, "SMS Scheduled for $dateTime", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Log.e("SMS_SCHEDULER", "Permission to schedule exact alarms denied", e)
            Toast.makeText(
                context,
                "Permission to schedule exact alarms is required. Please enable in Settings.",
                Toast.LENGTH_LONG
            ).show()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context is MainActivity) {
                Intent().also { settingsIntent ->
                    settingsIntent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(settingsIntent)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
    }
}

fun parsePhoneNumbers(phoneNumbersString: String): List<String> {
    return phoneNumbersString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
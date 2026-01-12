package com.example.smsschedulerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import java.util.Date

class MyBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "SMS_SCHEDULER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "BroadcastReceiver triggered at: ${Date(System.currentTimeMillis())}")

        val scheduledTime = intent.getLongExtra("scheduledTime", 0)
        val currentTime = System.currentTimeMillis()

        Log.d(TAG, "Scheduled time was: ${Date(scheduledTime)}")
        Log.d(TAG, "Time difference (seconds): ${(currentTime - scheduledTime) / 1000}")

        val phoneNumbers = intent.getStringArrayListExtra("phoneNumbers") ?: emptyList()
        val message = intent.getStringExtra("message") ?: ""

        Log.d(TAG, "Phone numbers: $phoneNumbers")
        Log.d(TAG, "Message: $message")

        if (phoneNumbers.isNotEmpty() && message.isNotEmpty()) {
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }

                // Instead of using dynamic broadcast receivers for sent/delivered status,
                // we'll just send the SMS and log it

                for (phoneNumber in phoneNumbers) {
                    Log.d(TAG, "Attempting to send SMS to: $phoneNumber")

                    try {
                        if (message.length > 160) {
                            val messageParts = smsManager.divideMessage(message)
                            smsManager.sendMultipartTextMessage(
                                phoneNumber,
                                null,
                                messageParts,
                                null,
                                null
                            )
                            Log.d(TAG, "Sent multipart SMS to $phoneNumber")
                        } else {
                            smsManager.sendTextMessage(
                                phoneNumber,
                                null,
                                message,
                                null,
                                null
                            )
                            Log.d(TAG, "Sent single SMS to $phoneNumber")
                        }

                        // Show success toast
                        Toast.makeText(context, "SMS sent to $phoneNumber", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send SMS to $phoneNumber: ${e.message}", e)
                        Toast.makeText(context, "Failed to send SMS to $phoneNumber: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in SMS sending process: ${e.message}", e)
                Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w(TAG, "No phone numbers or message to send")
            Toast.makeText(context, "No phone numbers or message to send", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.moneymaster.components

import java.text.SimpleDateFormat
import java.util.Locale

object Formats {
     val DATE_FORMAT = SimpleDateFormat("dd MMM. yyyy", Locale.US)
     val DATE_TIME_FORMAT = SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.US)
}
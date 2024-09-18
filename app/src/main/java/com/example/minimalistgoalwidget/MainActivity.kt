package com.example.minimalistgoalwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var goalEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var dateLabel: TextView
    private var selectedDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goalEditText = findViewById(R.id.goalEditText)
        saveButton = findViewById(R.id.saveButton)
        dateLabel = findViewById(R.id.dateLabel)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select target date")
            .build()

        dateLabel.setOnClickListener {
            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection
            updateDateLabel(selection)
        }

        saveButton.setOnClickListener {
            val goalText = goalEditText.text.toString()
            if (goalText.isNotEmpty() && selectedDate != 0L) {
                saveGoal(goalText, selectedDate)
                updateWidgets()
                finish()
            }
        }

        loadCurrentGoal()
    }

    private fun saveGoal(goal: String, date: Long) {
        val sharedPref = getSharedPreferences("GoalWidgetPref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("goal", goal)
            putLong("date", date)
            apply()
        }
    }

    private fun updateDateLabel(date: Long) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        dateLabel.text = "Target Date: ${dateFormat.format(Date(date))}"
    }

    private fun loadCurrentGoal() {
        val sharedPref = getSharedPreferences("GoalWidgetPref", Context.MODE_PRIVATE)
        val goal = sharedPref.getString("goal", null)
        val date = sharedPref.getLong("date", 0)

        if (goal != null && date != 0L) {
            goalEditText.setText(goal)
            selectedDate = date
            updateDateLabel(date)
        }
    }

    private fun updateWidgets() {
        val intent = Intent(this, GoalWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application)
            .getAppWidgetIds(ComponentName(application, GoalWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}

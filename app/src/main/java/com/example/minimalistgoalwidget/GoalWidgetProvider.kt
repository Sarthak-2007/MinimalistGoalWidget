package com.example.minimalistgoalwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.util.concurrent.TimeUnit

class GoalWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)
            
            val sharedPref = context.getSharedPreferences("GoalWidgetPref", Context.MODE_PRIVATE)
            val goal = sharedPref.getString("goal", null)
            val goalDate = sharedPref.getLong("date", 0)

            if (goal != null && goalDate != 0L) {
                val currentDate = System.currentTimeMillis()
                val daysLeft = TimeUnit.MILLISECONDS.toDays(goalDate - currentDate)

                views.setTextViewText(R.id.goalTitle, goal.toUpperCase())
                views.setTextViewText(R.id.daysLeft, daysLeft.toString())
                views.setTextViewText(R.id.daysLeftLabel, if (daysLeft == 1L) "DAY LEFT" else "DAYS LEFT")
            } else {
                views.setTextViewText(R.id.goalTitle, "SET A GOAL")
                views.setTextViewText(R.id.daysLeft, "--")
                views.setTextViewText(R.id.daysLeftLabel, "TAP TO START")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

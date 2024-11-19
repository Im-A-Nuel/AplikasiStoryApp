package com.coding.aplikasistoryapp.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.coding.aplikasistoryapp.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val storyRepository = Injection.provideStoryRepository(applicationContext)
        return StackRemoteViewsFactory(applicationContext, storyRepository)
    }
}
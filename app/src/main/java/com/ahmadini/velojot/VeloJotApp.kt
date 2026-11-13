package com.ahmadini.velojot

import android.app.Application
import com.ahmadini.velojot.data.local.database.VeloJotDatabase
import com.ahmadini.velojot.data.repository.NoteRepositoryImpl
import com.ahmadini.velojot.domain.repository.NoteRepository

class VeloJotApp : Application() {

    lateinit var database: VeloJotDatabase
        private set

    lateinit var noteRepository: NoteRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = VeloJotDatabase.getDatabase(this)
        noteRepository = NoteRepositoryImpl(database.noteDao())
    }
}

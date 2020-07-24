package com.example.laby.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.laby.database.ScoreDao
import java.lang.IllegalArgumentException

class GameViewModelFactory(
    private val scoreDB: ScoreDao,
    private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            return GameViewModel(scoreDB, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.laby.ranking

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.laby.database.ScoreDao
import java.lang.IllegalArgumentException

class RankingViewModelFactory(
    private val dataSource: ScoreDao,
    private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RankingViewModel::class.java)){
            return RankingViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
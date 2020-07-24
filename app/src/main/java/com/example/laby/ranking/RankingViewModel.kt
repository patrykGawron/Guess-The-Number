package com.example.laby.ranking

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.laby.database.Score
import com.example.laby.database.ScoreDao
import com.example.laby.formatScores
import kotlinx.coroutines.*

class RankingViewModel(
    val database: ScoreDao,
    application: Application): AndroidViewModel(application) {

    private val _backToGame = MutableLiveData<Boolean>()
    val backToGame: LiveData<Boolean>
        get() = _backToGame

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val ranks = database.getTop10()

    private val names = mutableListOf(
        "Patryk",
        "Jedrzej",
        "Tomek",
        "Szymon",
        "Grzesiu"
    )

    val scoresString = Transformations.map(ranks) { ranks ->
        formatScores(ranks, application.resources)
    }

    fun backToGame(){
        _backToGame.value = true
    }

    fun onAddEntry(){
        Log.i("RankingViewModel",
            "Adding")
        uiScope.launch {
            addNewScore()
        }
    }

    private suspend fun addNewScore(){
        withContext(Dispatchers.IO){
            var score = Score(
                (1..10).random(),
                names.random()
            )
            Log.i("RankingViewModel",
            "Adding ${score.username} ${score.score}")
            database.insert(score)

            val allScores = database.getAll()
            allScores.forEach {
                Log.i("RankingViewModel",
                "${it.username} \t ${it.score}")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
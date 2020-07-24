package com.example.laby.game

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.laby.database.Score
import com.example.laby.database.ScoreDao
import com.example.laby.ranking.RankingActivity
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.random.nextInt

class GameViewModel(
    private val scoreDB: ScoreDao,
    application: Application) : AndroidViewModel(application) {

    private val _switchToRanking = MutableLiveData<Boolean>()
    val switchToRanking: LiveData<Boolean>
        get() = _switchToRanking

    private val _hintText = MutableLiveData<String>()
    val hintText: LiveData<String>
        get() = _hintText

    private val _wrongRange = MutableLiveData<Boolean>()
    val wrongRange: LiveData<Boolean>
        get() = _wrongRange

    private val _gameWon = MutableLiveData<Boolean>()
    val gameWon: LiveData<Boolean>
        get() = _gameWon

    val sharedPref = application.getSharedPreferences("userData",
    Context.MODE_PRIVATE)

    var totalScore = sharedPref.getInt("currentScore", 0)
    val username = sharedPref.getString("username", "")

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var endGameTitle = ""
    var endGameMessage = ""
    var endGameButtonText = ""


    private var numberToGuess = 0
    private var score = 0
    private var attempts = 0

    init {
        onNewGame()
    }

    fun onRanking() {
        _switchToRanking.value = true;
    }

    fun onNewGame(){
        numberToGuess = Random.nextInt(IntRange(0, 20))
        attempts = 0
        score = 0
    }

    private suspend fun updateScores(){
        withContext(Dispatchers.IO){
            val score = Score(totalScore, username ?: "")
            scoreDB.insert(score)
        }
    }

    private fun finishGame(gameWon: Boolean){
        scoreTheGame()
        totalScore += score
        uiScope.launch {
            updateScores()
        }
        if(gameWon){
            endGameTitle = "Wygrałeś!"
            endGameMessage = "Udało Ci się trafić w $attempts próbach!"
            endGameButtonText = "Super!"
        } else {
            endGameTitle = "Przegrałeś!"
            endGameMessage = "Wykorzystałeś ponad 10 prób!"
            endGameButtonText = "Spróbuj ponownie"
        }
        _gameWon.value = gameWon
        onNewGame()
    }

    private fun scoreTheGame(){
        score = when(attempts){
            1 -> 5
            in 2..4 -> 3
            in 5..6 -> 2
            in 7..10 -> 1
            else -> 0
        }
    }

    fun onTry(view: View) {
        val rootView = view.rootView
        val guess = rootView.editText?.text?.toString() ?: "-1"
        if (guess.toInt() in 0..20) {
            attempts++
            if(attempts > 10){
                finishGame(false)
                return
            }
            if (guess.toInt() == numberToGuess) {
                finishGame(true)
            } else {
                if (guess.toInt() > numberToGuess) {
                    _hintText.value = "Mniej! Strzelałeś już $attempts razy"
                } else {
                    _hintText.value = "Więcej! Strzelałeś już $attempts razy"
                }
            }
        } else {
            _wrongRange.value = true
        }
    }

}
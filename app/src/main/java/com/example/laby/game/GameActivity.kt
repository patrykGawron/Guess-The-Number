package com.example.laby.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.laby.R
import com.example.laby.database.UserDatabase
import com.example.laby.databinding.ActivityGameBinding
import com.example.laby.ranking.RankingActivity
import kotlin.random.Random
import kotlinx.android.synthetic.main.activity_game.*
import kotlin.random.nextInt

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val application = requireNotNull(this).application

        val dataSource = UserDatabase.getInstance(application).scoreDao
        val gameViewModelFactory = GameViewModelFactory(dataSource, application)

        gameViewModel = ViewModelProviders.of(this, gameViewModelFactory)
            .get(GameViewModel::class.java)

        binding.gameViewModel = gameViewModel

        binding.lifecycleOwner = this

        gameViewModel.switchToRanking.observe(this, Observer<Boolean> { switch ->
            if(switch){
                val intent = Intent(this, RankingActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        gameViewModel.wrongRange.observe(this, Observer<Boolean> { wrongRange ->
            if(wrongRange){
                Toast.makeText(applicationContext,
                    "Liczba powinna być z zakresu <0,20>",
                    Toast.LENGTH_LONG).show()
            }
        })

        gameViewModel.gameWon.observe(this, Observer<Boolean> { gameWon ->
            showDialog()
        })

    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(this@GameActivity)

        builder.setTitle(gameViewModel.endGameTitle)
        builder.setMessage(gameViewModel.endGameMessage)

        builder.setPositiveButton(gameViewModel.endGameButtonText){dialog, which ->
            Toast.makeText(applicationContext,"Wylosowano nową liczbę", Toast.LENGTH_SHORT).show()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

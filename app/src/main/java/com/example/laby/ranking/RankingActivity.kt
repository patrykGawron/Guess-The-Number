package com.example.laby.ranking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.laby.R
import com.example.laby.database.UserDatabase
import com.example.laby.databinding.ActivityRankingBinding
import com.example.laby.game.GameActivity

class RankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val application = requireNotNull(this).application

        // Create an instance of the ViewModel Factory.
        val dataSource = UserDatabase.getInstance(application).scoreDao
        val viewModelFactory = RankingViewModelFactory(dataSource, application)

        val rankingViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(RankingViewModel::class.java)

        binding.rankingViewModel = rankingViewModel
        binding.lifecycleOwner = this

        rankingViewModel.backToGame.observe(this, Observer<Boolean> { backToGame ->
            if(backToGame){
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}
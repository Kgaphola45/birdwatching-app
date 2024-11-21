

package com.example.st10180168_st10102524_wonder_wings.Game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.st10180168_st10102524_wonder_wings.ChallengeModel
import com.example.st10180168_st10102524_wonder_wings.Hotpots
import com.example.st10180168_st10102524_wonder_wings.R
import com.example.st10180168_st10102524_wonder_wings.ToolBox
import com.example.st10180168_st10102524_wonder_wings.databinding.ActivityGameBinding

//this is the main activity used to host the duck hunt game
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameView: GameView
    private lateinit var startGame: TextView

    //==============================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gameView = binding.gameView
        startGame = binding.startGame

        ChallengeModel.newTopRoundInDuckHunt = 0

        view.setOnClickListener(){
            run()
        }
    }

    //==============================================================================================
    private fun run(){
        startGame.isVisible = false
        gameView.isVisible = true
        gameView.start()
    }

    //==============================================================================================
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        ChallengeModel.saveChallenge()
        gameView.stop()
        val intent = Intent(this, Hotpots::class.java)
        startActivity(intent)
    }
}
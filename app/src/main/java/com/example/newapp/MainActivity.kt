package com.example.newapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.security.AccessController.getContext
import androidx.lifecycle.ViewModelProviders


class MainActivity : ComponentActivity() {
    private	lateinit	var	hintBtn: Button
    private	lateinit var letterButtons: List<Button>
    private var hintStatus:Int=0
    private lateinit var hangmanViewModel:HangmanViewModel
    private var word:TextView=findViewById(R.id.word)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        //hangmanViewModel	=	ViewModelProvider(this).get(HangmanViewModel::class.java)
        //word.setText(hangmanViewModel.get1Word())
        //val id: Int = resources.getIdentifier("btnA", "id", packageName)
        hintBtn=findViewById(R.id.hintBtn)
        hintBtn.setOnClickListener{
            hintBtnClicked(hintStatus);
        }

        letterButtons = ('A'..'Z').map { char ->
            val buttonId = resources.getIdentifier("btn"+char.toString(), "id", packageName)
            findViewById<Button>(buttonId)
        }
        letterButtons.forEach { button ->
            button.setOnClickListener {
                val buttonText = button.text.toString()
                letterClicked(buttonText.first())
            }
        }
        }

    private fun hintBtnClicked(hintStatus: Int) {

    }

    private fun letterClicked(buttonText: Char) {
        Toast.makeText(
            this,
            buttonText.toString(),
            Toast.LENGTH_SHORT)
            .show()
        word.setText(buttonText.toString())
        //hangmanViewModel.set1Word(buttonText.toString())
    }

}

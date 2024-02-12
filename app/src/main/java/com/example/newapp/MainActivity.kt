package com.example.newapp


import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.content.Context
import androidx.fragment.app.DialogFragment

import androidx.lifecycle.ViewModelProvider


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    //Components on the screen
    private	lateinit var hintBtn: Button
    private	lateinit var newGameButton: Button
    private	lateinit var letterButtons: List<Button>
    private lateinit var word:TextView
    private lateinit var hangmanImage:ImageView
    private lateinit var hintMsg:TextView
    //private lateinit var dialog:Dialog
    //private lateinit var messageTextView:TextView
    //private lateinit var okButton:Button
    private lateinit var builder:AlertDialog.Builder

    //Game status parameters
    private var targetWord:String = ""
    private var hintStatus:Int=0 //0-3
    private var hangmanStatus:Int=1 //0-10
    private var hintString:String="start"
    private var usedLetters = Array<Boolean>(26){false}

    //ViewModel
    private lateinit var hangmanViewModel:HangmanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        //Get ViewModel
        hangmanViewModel = ViewModelProvider(this).get(HangmanViewModel::class.java)

        //Connect components to View and add button listeners
        word=findViewById(R.id.word)
        word.setTypeface(null, Typeface.BOLD)
        word.paintFlags = word.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        hintMsg=findViewById(R.id.hintMsg)
        hangmanImage=findViewById(R.id.imageview1)

        builder= AlertDialog.Builder(this)
        builder.setTitle("Game ended")

        builder.setPositiveButton("Start a new game") { dialog, which ->
            // Do something when the user clicks the OK button
            ngBtnClicked()
        }

        newGameButton=findViewById(R.id.NGBtn)
        newGameButton.setOnClickListener{
            ngBtnClicked();
        }
        hintBtn=findViewById(R.id.hintBtn)
        hintBtn.setOnClickListener{
            hintBtnClicked();
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

        //Load game status parameters from ViewModel and start a new game
        loadViewModel()
        }

    override fun onDestroy() {
        super.onDestroy()
        saveViewModel()
    }
    private fun loadViewModel() {
        //load all the game status parameters from viewModel
        targetWord = hangmanViewModel.get1Word()
        usedLetters = hangmanViewModel.usedLetters
        hintString = hangmanViewModel.hintMessage
        hintStatus = hangmanViewModel.hintStatus
        hangmanStatus = hangmanViewModel.hangmanStatus

        setHangmanImage(hangmanStatus)
        setHintMsg(hintString)
        enableAllButtons()
        setGuessedWord(" ".repeat(targetWord.length))

        for(i in 0 until 26) {
            if (usedLetters[i])
                letterClicked('A'+i)
        }
        Log.d(TAG,	"load to view model")
    }
    private fun saveViewModel() {
        //load all the game status parameters from viewModel
        hangmanViewModel.set1Word(targetWord)
        hangmanViewModel.usedLetters = usedLetters
        hangmanViewModel.hintMessage = hintString
        hangmanViewModel.hintStatus = hintStatus
        hangmanViewModel.hangmanStatus = hangmanStatus
        Log.d(TAG,	"save to view model")
    }

    //UI->backend interfaces:
    private fun ngBtnClicked() {
        //When the new game button is clicked, this function will be called
        //Parameters:
        //  None
        hangmanViewModel.renew()
        refresh()
        loadViewModel()
    }

    private fun refresh(){
        // set layout view to empty
        setHangmanImage(0)
        setHintMsg("")
        enableAllButtons()
        setGuessedWord(" ".repeat(targetWord.length))
    }

    private fun hintBtnClicked() {
        //When the hint button is clicked, this function will be called
        //Parameters:
        //  None
        //TODO: to be implemented
    }

    private fun letterClicked(buttonText: Char) {
        //When a letter button is clicked, this function will be called
        //Parameters:
        //  buttonText: The letter(capital) being clicked

        usedLetters[buttonText-'A'] = true
        var displayWord = word.text.toString().toCharArray()
        var letterHit = false
        for (i in 0 until targetWord.length) {
            if(targetWord[i]==buttonText) {
                displayWord[i] = buttonText
                letterHit = true
            }
        }
        if (!letterHit){
            setHangmanImage(++hangmanStatus)
            if(hangmanStatus==10){

                builder.setMessage("You lost the game!")
                val dialog = builder.create()
                // Display the dialog
                dialog.show()

            }
        }

        else{
            word.setText(String(displayWord))
            if(!displayWord.contains(' ')){
                //win game

                builder.setMessage("Congratulations! You won the game!")
                val dialog = builder.create()
                // Display the dialog
                dialog.show()
            }
        }



        disableButtonByLetter(buttonText)

    }

    //backend->UI interfaces:
    private fun setHintMsg(_hintString:String){
        hintMsg.setText("Hint: "+_hintString)
    }
    private fun setGuessedWord(_word:String){
        //_word should be a combination of spaces and guessed letters, eg. "  AM l "
        word.setText(_word)
    }
    private fun enableAllButtons(){
        letterButtons.forEach { button ->
            button.isEnabled=true
        }
        hintBtn.isEnabled=true
    }
    private fun disableButton(button: Button) {
        //When this function is called, the button will be disabled
        //Parameters:
        //    button: The button that you want to disable

        button.isEnabled = false
    }
    private fun disableHintButton() {
        //When this function is called, the hint button will be disabled
        //Parameters:
        //    None
        disableButton(hintBtn)
    }
    private fun disableButtonByLetter(letter: Char) {
        //When this function is called, the button corresponding the input letter will be disabled
        //Parameters:
        //      letter: The letter(capital) the button to which you want to disable

        letterButtons.forEach { button ->
            if (button.text.first() == letter) {
                disableButton(button)
            }
        }
    }
    private fun setHangmanImage(imgNumber:Int){
        //This function sets hangman image
        //Parameters:
        //  imgNumber: Should be an Int between 0-10.
        // TODO: check what's wrong here

        val resourceId = resources.getIdentifier("i"+imgNumber.toString(), "drawable", packageName)
        hangmanImage.setImageResource(resourceId)
        Log.d(TAG,	"set img to "+"i"+imgNumber.toString())
    }
}


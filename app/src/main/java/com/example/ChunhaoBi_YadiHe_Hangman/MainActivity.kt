package com.example.ChunhaoBi_YadiHe_Hangman


import android.app.AlertDialog
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toBitmap

import androidx.lifecycle.ViewModelProvider
import com.example.ChunhaoBi_YadiHe_Hangman.databinding.LayoutBinding

private const val TAG = "MainActivity"
private const val LETTER_NUM = 26

class MainActivity : ComponentActivity() {
    private lateinit var binding: LayoutBinding

    //Components on the screen
    private	lateinit var letterButtons: List<Button>
    private lateinit var builder:AlertDialog.Builder

    //Game status parameters
    private lateinit var targetWord:String
    private var hintStatus:Int = 0 //0-3
    private var hangmanStatus:Int = 0 //0-10
    private lateinit var hintString:String
    private var usedLetters = Array(LETTER_NUM){false}

    //ViewModel
    private lateinit var hangmanViewModel:HangmanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get ViewModel
        hangmanViewModel = ViewModelProvider(this)[HangmanViewModel::class.java]

        //Connect components to View and add button listeners
        binding.word.setTypeface(null, Typeface.BOLD)
        binding.word.paintFlags = binding.word.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        builder= AlertDialog.Builder(this)
        builder.setTitle("Game ended")

        builder.setPositiveButton("Start a new game") { _, _ ->
            // Do something when the user clicks the OK button
            ngBtnClicked()
        }

        binding.NGBtn.setOnClickListener{
            ngBtnClicked()
        }
        binding.hintBtn.setOnClickListener{
            hintBtnClicked()
        }
        letterButtons = ('A'..'Z').map { char ->
            val buttonId = resources.getIdentifier("btn"+char.toString(), "id", packageName)
            findViewById(buttonId)
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
        targetWord = getString(hangmanViewModel.word)
        hintString = getString(hangmanViewModel.hintMessage)
        usedLetters = hangmanViewModel.usedLetters
        hintStatus = hangmanViewModel.hintStatus
        hangmanStatus = hangmanViewModel.hangmanStatus

        setHangmanImage(hangmanStatus)
        enableAllButtons()
        setGuessedWord(" ".repeat(targetWord.length))
        if (hintStatus >= 1) {
            setHintMsg(hintString)
            if (hintStatus >= 3) {
                disableHintButton()
            }
        }


        var displayWord = binding.word.text.toString()
        for(i in 0 until LETTER_NUM) {
            if (usedLetters[i]) {
                displayWord = revealText(displayWord, 'A'+i)
            }
        }
        //binding.word.text = displayWord
        setGuessedWord(displayWord)

        Log.d(TAG,	"loaded view model")
    }
    private fun revealText(curStr:String, buttonText:Char): (String) {
        val displayWord = curStr.toCharArray()
        for (j in targetWord.indices) {
            if(targetWord[j]==buttonText) {
                displayWord[j] = buttonText
            }
        }
        disableButtonByLetter(buttonText)
        return String(displayWord)
    }
    private fun saveViewModel() {
        //load all the game status parameters from viewModel
//        hangmanViewModel.word = targetWord
        hangmanViewModel.usedLetters = usedLetters
//        hangmanViewModel.hintMessage = hintString
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
        loadViewModel()
        refresh()
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
        when (hintStatus) {
            0 -> {
                setHintMsg(hintString)
                hintStatus++
            }
            1 -> {
                if (hangmanStatus == 9)
                {
                    Toast.makeText(this, "Hint not available!", Toast.LENGTH_SHORT).show()
                    return
                }
                var disable = false
                for (i in 0 until  LETTER_NUM) {
                    if (!usedLetters[i] && 'A'+ i !in targetWord) {
                        disable = if (disable) {
                            disableButtonByLetter('A'+ i)
                            false
                        } else
                            true
                    }
                }
                setHangmanImage(++hangmanStatus)
                hintStatus++
            }
            2 -> {
                if (hangmanStatus == 9)
                {
                    Toast.makeText(this, "Hint not available!", Toast.LENGTH_SHORT).show()
                    return
                }
                var displayWord = binding.word.text.toString()
                for (letter in "AEIOU") {
                    usedLetters[letter-'A'] = true
                    displayWord = revealText(displayWord, letter)
                    if (checkWin(displayWord))
                        return
                }
                //binding.word.text = displayWord
                setGuessedWord(displayWord)
                setHangmanImage(++hangmanStatus)
                hintStatus++
                disableHintButton()
            }
            else -> {
//                Toast.makeText(this, "Hint not available!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun letterClicked(buttonText: Char) {
        //When a letter button is clicked, this function will be called
        //Parameters:
        //  buttonText: The letter(capital) being clicked

        usedLetters[buttonText-'A'] = true
        val displayWord = revealText(binding.word.text.toString(), buttonText)
        if (displayWord == binding.word.text.toString()){
            setHangmanImage(++hangmanStatus)
        }
        else{
            //binding.word.text = displayWord
            setGuessedWord(displayWord)
            checkWin(displayWord)
        }
        disableButtonByLetter(buttonText)
    }
    private fun checkWin(word:String): Boolean {
        if(!word.contains(' ')){
            //win game
            builder.setMessage("Congratulations! You won the game!")
            val dialog = builder.create()
            // Display the dialog
            dialog.show()
            return true
        }
        return false
    }

    //backend->UI interfaces:
    private fun setHintMsg(hintString:String){
        binding.hintMsg.text = "Hint: "+hintString
    }
    private fun setGuessedWord(word:String){
        val density = resources.displayMetrics.density

        //_word should be a combination of spaces and guessed letters, eg. "  AM l "
        binding.word.text = word
        Log.d(TAG,	"Set word to "+word)

        val linearLayout = findViewById<LinearLayout>(R.id.wordBox)

        linearLayout.removeAllViews();
        for (char in word) {
            val textView = TextView(this)
            val textSizeInSp = 16 // Set your desired text size in sp
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp.toFloat())
            val scaledPixelsToPixels = textSizeInSp * density
            val widthInPixels = (scaledPixelsToPixels* 2.5).toInt() // Adjust the multiplier as needed
            val heightInPixels = widthInPixels
            val margin=(scaledPixelsToPixels*0.25).toInt()
            val padding = 0
            textView.setText(char.toString())
            textView.width = widthInPixels
            textView.height = heightInPixels
            //textView.setBackgroundColor(Color.GRAY)
            textView.gravity = Gravity.CENTER
            val drawable = resources.getDrawable(R.drawable.underline, null) // Replace `your_image` with the name of your image file
            val bitmapDrawable = BitmapDrawable(resources, drawable.toBitmap(widthInPixels, heightInPixels, Bitmap.Config.ARGB_8888))
            textView.background = bitmapDrawable

            // Set padding to ensure the text is centered both horizontally and vertically
             // Adjust padding as needed
            textView.setPadding(padding, padding, padding, padding)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(margin, margin, margin, margin)
            textView.layoutParams = layoutParams

            linearLayout.addView(textView)
        }
    }
    private fun enableAllButtons(){
        letterButtons.forEach { button ->
            button.isEnabled=true
        }
        binding.hintBtn.isEnabled=true
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
        disableButton(binding.hintBtn)
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

        val resourceId = resources.getIdentifier("i"+imgNumber.toString(), "drawable", packageName)
        binding.imageview1.setImageResource(resourceId)
        Log.d(TAG,	"set img to "+"i"+imgNumber.toString())
        if(hangmanStatus==10){
            builder.setMessage("You lost the game!")
            val dialog = builder.create()
            // Display the dialog
            dialog.show()
        }
    }
}


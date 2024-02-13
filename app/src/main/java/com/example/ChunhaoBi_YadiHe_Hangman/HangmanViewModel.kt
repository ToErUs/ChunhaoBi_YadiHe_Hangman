package com.example.ChunhaoBi_YadiHe_Hangman

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel

class HangmanViewModel: ViewModel() {
    val word:Int
        get() = questionBank[currentIndex].textResId
    var hintStatus:Int=0
    var hangmanStatus:Int=0
    var usedLetters = Array<Boolean>(26){false}
    val hintMessage:Int
        get() = questionBank[currentIndex].hintResId

    private val questionBank = listOf(
        WordPuzzle(R.string.chicken, R.string.chicken_hint),
        WordPuzzle(R.string.australia, R.string.australia_hint),
        WordPuzzle(R.string.football, R.string.football_hint),
        WordPuzzle(R.string.happy, R.string.happy_hint),
        WordPuzzle(R.string.triangle, R.string.triangle_hint),
    )
    private var currentIndex:Int=0
    init	{
        Log.d(TAG,	"ViewModel	instance created")
//        hintMessage="example hint"
    }

    public fun renew(){
        //set all parameters to initial status
        //TODO:to be implemented
        usedLetters.fill(false)
        hintStatus = 0
        hangmanStatus = 0
        currentIndex = (currentIndex + 1) % questionBank.size
    }
}
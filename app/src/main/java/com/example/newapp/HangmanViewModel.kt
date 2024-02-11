package com.example.newapp

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel

class HangmanViewModel: ViewModel() {


    var word:String="EXAMPLE"
    private var hintStatus:Int=0
    private var hangmanStatus:Int=0
    private lateinit var usedLetters: List<Char>

    init	{
        Log.d(TAG,	"ViewModel	instance	created")
        usedLetters= emptyList()
    }

    public fun renew(){
        //set all parameters to initial status
        //TODO:to be implemented
    }

    public fun get1Word(): String {
        return word
    }
    public fun set1Word(_word:String){
        word=_word
    }

    //TODO:and other interfaces...



}
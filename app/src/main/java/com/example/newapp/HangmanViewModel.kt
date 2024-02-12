package com.example.newapp

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel

class HangmanViewModel: ViewModel() {
    var word:String="EXAMPLE"
    var hintStatus:Int=0
    var hangmanStatus:Int=0
    var usedLetters = Array<Boolean>(26){false}
    var hintMessage:String = "example hint"

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
        hintMessage = ""
    }

    public fun get1Word(): String {
        return word
    }
    public fun set1Word(_word:String){
        word=_word
    }

    //TODO:and other interfaces...



}
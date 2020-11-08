package com.jmips.bobi

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.jmips.bobi.SimpleFragment.Companion.newInstance


private const val TAG = "MainActivity"
private const val REQUEST_IMAGE_CAPTURE = 100

class MainActivity : AppCompatActivity() {

    private lateinit var timer : CountDownTimer
    private var untilFinished = 10000L
    private var isFragmentDisplayed: Boolean=false
    private lateinit var mButton: Button
    val STATE_FRAGMENT = "state_of_fragment"
    private lateinit var myusername:String

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        // Save the state of the fragment (true=open, false=closed).
        savedInstanceState.putBoolean(STATE_FRAGMENT, isFragmentDisplayed)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myusername=intent.getStringExtra(USERNAME)

        startCountDownTimer(untilFinished)
        mButton= findViewById<Button>(R.id.fragment_button)

        mButton.setOnClickListener {
            if (!isFragmentDisplayed){
                displayFragment()
            }else{
                closeFragment()
            }

        }
        if (savedInstanceState != null) {
            isFragmentDisplayed =
                savedInstanceState.getBoolean(STATE_FRAGMENT);
            if (isFragmentDisplayed) {
                // If the fragment is displayed, change button to "close".
                mButton.setText(R.string.close_fragment_button_text);
            }
        }

        findViewById<Button>(R.id.camera_button).setOnClickListener {
            openNativeCamera()
        }

        findViewById<Button>(R.id.details_button).setOnClickListener {
            openDetailsActivity()
        }

        findViewById<Button>(R.id.dialog_button).setOnClickListener {
            val myalertDialog = AlertDialog.Builder(this)
            with(myalertDialog){
                setTitle("Bobi")
                setMessage(getString(R.string.dialog_txt))
                apply{
                    setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                        Toast.makeText(this@MainActivity, "You selected OK", Toast.LENGTH_SHORT)
                            .show()
                    })
                    setNegativeButton("Cancel"){ _, _ ->
                        Log.d(TAG, "Dialog cancelled")
                    }}

                create().show()
            }
        }

        findViewById<Button>(R.id.snackbar_button).setOnClickListener {
            showAppSnackbar()
        }
    }

    override fun onResume() {
        super.onResume()
        startCountDownTimer(untilFinished)
    }

    override fun onPause() {
        super.onPause()

        timer.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap =data?.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun openNativeCamera(){
        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun openDetailsActivity(){
        val intent = Intent(this, DetailsActivity::class.java)
        startActivity(intent)
    }

    private fun startCountDownTimer(time: Long){
        timer = object: CountDownTimer(time, 1000){
            override fun onTick(millisUntilFinished: Long) {
                untilFinished = millisUntilFinished
                findViewById<TextView>(R.id.countdown).text="Seconds remaining: ${millisUntilFinished/1000}"
            }

            override fun onFinish() {
                findViewById<TextView>(R.id.countdown).text="Done"

            }

        }

        timer.start()

    }

    private fun showAppSnackbar() {
        val mySnackBar = Snackbar.make(
            findViewById(R.id.myCoordinatorLayout),
            R.string.dialog_txt,
            Snackbar.LENGTH_LONG
        )
        mySnackBar.setAction(R.string.snackbar_txt) {
            Toast.makeText(applicationContext, R.string.snackbar_txt, Toast.LENGTH_SHORT).show()
        }
        mySnackBar.show()
    }

    fun displayFragment(){
         val simpleFragment = newInstance()

        // Get the FragmentManager and start a transaction.
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager
            .beginTransaction()

        // Add the SimpleFragment.
        fragmentTransaction.add(R.id.fragment_container, simpleFragment).addToBackStack(null).commit()

        // Update the Button text.
        mButton.setText(R.string.close_fragment_button_text)
        // Set boolean flag to indicate fragment is open.
        isFragmentDisplayed = true;
    }

    fun closeFragment(){
        // Get the FragmentManager.
        val fragmentManager = supportFragmentManager
        // Check to see if the fragment is already showing.
        val simpleFragment = fragmentManager
            .findFragmentById(R.id.fragment_container) as SimpleFragment?
        if (simpleFragment != null) {
            // Create and commit the transaction to remove the fragment.
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(simpleFragment).commit()
        }
        // Update the Button text.
        mButton.setText(R.string.open_fragment_button_text)
        // Set boolean flag to indicate fragment is closed.
        isFragmentDisplayed = false
    }
}
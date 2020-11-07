package com.jmips.bobi

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.snackbar.Snackbar
import java.security.AccessController.getContext

private const val TAG = "MainActivity"
private const val REQUEST_IMAGE_CAPTURE = 100

class MainActivity : AppCompatActivity() {

    private lateinit var timer : CountDownTimer
    private var untilFinished = 10000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    setPositiveButton("OK",DialogInterface.OnClickListener{ _, _ ->
                    Toast.makeText(this@MainActivity,"You selected OK",Toast.LENGTH_SHORT).show()
                })
                    setNegativeButton("Cancel"){_,_ ->
                        Log.d(TAG,"Dialog cancelled")
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
        val intent = Intent(this,DetailsActivity::class.java)
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
        val mySnackBar = Snackbar.make(findViewById(R.id.myCoordinatorLayout),R.string.dialog_txt,Snackbar.LENGTH_LONG)
        mySnackBar.setAction(R.string.snackbar_txt) {
            Toast.makeText(applicationContext, R.string.snackbar_txt, Toast.LENGTH_SHORT).show()
        }
        mySnackBar.show()
    }
}
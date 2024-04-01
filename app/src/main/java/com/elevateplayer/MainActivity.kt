package com.elevateplayer

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    private lateinit var minimizeBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        minimizeBtn = findViewById(R.id.minimize)

        if(isServiceRunning()){
            stopService(Intent(this@MainActivity, ElevatePlayerApp::class.java))
        }

        minimizeBtn.setOnClickListener{
            if(checkOverlayPermission()){
                startService(Intent(this@MainActivity, ElevatePlayerApp::class.java))
                finish()
            }else{
                requestFloatingWindowPermission()
            }
    }
}

    private fun isServiceRunning(): Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        @SuppressWarnings("deprecation")
        for(service in manager.getRunningServices(Int.MAX_VALUE)){
            return ElevatePlayerApp::class.java.name == service.service.className
        }
        return false;
    }

    private fun requestFloatingWindowPermission(){
        val builder = AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Screen overlay permission needed")
        builder.setMessage("Enable display for the app from settings")
        builder.setPositiveButton("Open Settings", DialogInterface.OnClickListener { dialog, which ->

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, RESULT_OK)
        })
        dialog = builder.create()
        dialog.show()
    }

    private fun checkOverlayPermission():Boolean{
        return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            Settings.canDrawOverlays(this)
        } else {
            return false
        }
    }
}
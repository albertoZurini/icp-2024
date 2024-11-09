package com.example.hce_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hce_app.ui.theme.NFCDemoTheme

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // val action: String? = intent?.action
        try {
            val data: Uri? = intent?.data
            val dataToSend = data.toString().substring(14)
            Log.i(TAG, dataToSend)
            if (dataToSend.length > 0) {
                // Switch to the HCE activity and set the variable
                val intent = Intent(this, HCEActivity::class.java)
                intent.putExtra("data", dataToSend)
                startActivity(intent)
            }
        } catch (e: Exception) {}

        setContent {
            NFCDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen(
                        onNavigateToReader = {
                            val intent = Intent(this, NFCReaderActivity::class.java)
                            startActivity(intent)
                        },
                        onNavigateToHCE = {
                            val intent = Intent(this, HCEActivity::class.java)
                            startActivity(intent)
                        },
                        onNavigateToFIAT = {
                            val intent = Intent(this, FIATActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToReader: () -> Unit,
    onNavigateToHCE: () -> Unit,
    onNavigateToFIAT: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNavigateToReader,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Go to NFC Reader")
        }

        Button(
            onClick = onNavigateToHCE,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Go to HCE (Host Card Emulation)")
        }
/*
        Button(
            onClick = onNavigateToFIAT,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Receive FIAT payment")
        }
        */
    }
}

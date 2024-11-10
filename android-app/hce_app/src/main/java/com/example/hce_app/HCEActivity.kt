package com.example.hce_app

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.hce_app.cardEmulation.KHostApduService

class HCEActivity : ComponentActivity() {
    private val TAG = "HCEActivity"

    private var nfcAdapter: NfcAdapter? = null

    private var nfcMessage: String by mutableStateOf("Hello")

    private var address: String by mutableStateOf("GBXU6KDGPVMYRMG5CQ7FRX7HP2XCDVH3SQH3JNS4YQJ7HCVD2HI25PPK")
    private var amount: String by mutableStateOf("10")
    private var chainID: String by mutableStateOf("0x18e")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val b = intent.extras
            val data = b!!.getString("data").toString()
            if (data.length > 0) {
                if (data != null) {
                    setNFCMessage(data)
                }
            }
        } catch(e: Exception) {}


        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null || !supportNfcHceFeature()) {
            setContent {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Can't get NFCAdapter")
                }
            }
        }

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column {
                    Text(nfcMessage)
                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address where to receive the payment") }
                    )
                    TextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount to be paid") }
                    )
                    /*
                    TextField(
                        value = chainID,
                        onValueChange = { chainID = it },
                        label = { Text("chainID") }
                    )
                    */
                    Button(
                        onClick={
                            setNFCMessage()
                        }
                    ) {
                        Text("Set the message")
                    }
                }
                // TODO: Shazam should add here the GUI information for when the transaction has been made as well as for WalletConnect
            }
        }
    }

    private fun supportNfcHceFeature() =
        checkNFCEnable() && packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)

    private fun checkNFCEnable(): Boolean {
        return if (nfcAdapter == null) {
            false
        } else {
            nfcAdapter?.isEnabled == true
        }
    }

    private fun setNFCMessage(message: String = "") {
        // Combine all the data into a metamask url
        var urlToCast: String = "https://wrqdv-lqaaa-aaaak-qtpwa-cai.icp0.io/send?address=rh7rl-ah7re-omr2b-tvrei-me7hw-gojty-36z47-si6hs-mktds-rhiq5-iqe&amount=0.01122"
        /*
        if(message.length == 0) {
            urlToCast = "web+stellar:$address?amount=$amount";
        } else {
            urlToCast = message
        }
        */
        Log.i(TAG, urlToCast)
        if (TextUtils.isEmpty(urlToCast)) {
            Toast.makeText(
                this,
                "The message has not to be empty",
                Toast.LENGTH_LONG,
            ).show()
        } else {
            Toast.makeText(
                this,
                urlToCast,
                Toast.LENGTH_LONG,
            ).show()
            val intent = Intent(this, KHostApduService::class.java)
            intent.putExtra("ndefMessage", urlToCast)
            startService(intent)
        }

        // TODO: Shazam after this point should check on BlockScout for the transaction to be made or add an event listener
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableNfcForegroundDispatch()
    }

    private fun enableNfcForegroundDispatch() {
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

}
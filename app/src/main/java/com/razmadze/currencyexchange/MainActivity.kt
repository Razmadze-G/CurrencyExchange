package com.razmadze.currencyexchange

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.R.layout.support_simple_spinner_dropdown_item
import com.razmadze.currencyexchange.models.AllData
import com.razmadze.currencyexchange.models.Currency
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val strURL: String= "https://nbg.gov.ge/gw/api/ct/monetarypolicy/currencies/ka/json"

    private val jsonHelper = JSONHelper()
    private var allData: AllData? = null
    private val currencyCodes: MutableMap<String, Double> = TreeMap()

    private lateinit var currencyToBeConverted: EditText
    private lateinit var convertTo: Spinner
    private lateinit var convertFrom: Spinner
    private lateinit var currencyConverted: TextView
    private lateinit var convertButton: Button
    private lateinit var currencyChangeButton: ImageButton
    private lateinit var colorThemeChangeButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        currencyToBeConverted = findViewById(R.id.currency_to_be_converted) // initializing view elements
        convertTo = findViewById(R.id.convert_to)
        convertFrom = findViewById(R.id.convert_from)
        currencyConverted = findViewById(R.id.currency_converted)
        convertButton = findViewById(R.id.button)
        currencyChangeButton = findViewById(R.id.switch_currency_button)
        colorThemeChangeButton = findViewById(R.id.color_theme_change_button)

        allData = jsonHelper.deserializeJSON(strURL) // sending request, getting response and parsing it into map
        fillSpinnerWithCurrencyCodes(allData)

        if(networkStatusIsNotAvailable(applicationContext)) {
            showNetworkErrorDialog()
        }

        if (currencyCodes.isEmpty()) {
            showResponseErrorDialog()
            return
        }

        convertButton.setOnClickListener{onConvert()} // setting onConvert function as onClick action for convert button
        currencyChangeButton.setOnClickListener{onSwitchCurrency()} // setting onSwitchCurrency function as onClick action for currencies change button
        colorThemeChangeButton.setOnClickListener{onColorThemeChange()} // setting onColorThemeChange function as onClick action for color theme change button
    }

    private fun fillSpinnerWithCurrencyCodes(allData:AllData?){
        val currencyList: List<Currency> = allData?.currencies ?: listOf()

        currencyCodes["GEL - ქართული ლარი"] = 1.0
        for(currency in currencyList) {
            currencyCodes[currency.code + " - " + currency.name] = (currency.rate / currency.quantity)
        }

        val adapter = ArrayAdapter (this, support_simple_spinner_dropdown_item, currencyCodes.keys.toList())

        convertFrom.adapter = adapter
        convertTo.adapter = adapter

        convertTo.setSelection(currencyCodes.keys.indexOf("GEL - ქართული ლარი"))
        convertFrom.setSelection(currencyCodes.keys.indexOf("USD - აშშ დოლარი"))
    }

    private fun onConvert() {
        if (currencyToBeConverted.text.toString().isEmpty()) {
            Toast.makeText(this, "Please provide amount", Toast.LENGTH_SHORT).show()
            return
        }
        if (currencyCodes.isEmpty()) {
            Toast.makeText(this, "Sorry, data about currencies are not present. Please try it later.", Toast.LENGTH_LONG).show()
            return
        }
        val inputValue = currencyToBeConverted.text.toString().toDouble()
        val selectedTo = currencyCodes[convertTo.selectedItem.toString()]
        val selectedFrom = currencyCodes[convertFrom.selectedItem.toString()]
        val resultValue = inputValue * selectedFrom!! / selectedTo!!
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING
        val result = "Result: ${df.format(resultValue)}"
        currencyConverted.text = result
    }

    private fun onSwitchCurrency() {
        if(convertTo.selectedItemPosition == convertFrom.selectedItemPosition){
            Toast.makeText(this, "Both currencies are identical, so change is not made!", Toast.LENGTH_SHORT).show()
            return
        }
        val temp = convertTo.selectedItemPosition
        convertTo.setSelection(convertFrom.selectedItemPosition)
        convertFrom.setSelection(temp)
    }

    private fun onColorThemeChange() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    @Suppress("DEPRECATION")
    private fun networkStatusIsNotAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        if (netInfo != null && netInfo.isConnected) return false
        return true
    }

    private fun showNetworkErrorDialog() {
        val spaces = "             "
        AlertDialog.Builder(this@MainActivity)
            .setMessage("No network connection. Do you want to exit?")
            .setTitle("Network Failure")
            .setCancelable(false)
            .setPositiveButton("Yes, Please") { _, _ -> finish() }
            .setNegativeButton("No, Thanks $spaces") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun showResponseErrorDialog() {
        val spaces = "             "
        AlertDialog.Builder(this@MainActivity)
            .setMessage("Data about currencies exchange rates are not available for now. Sorry for the inconvenience. Do you want to exit?")
            .setTitle("Data Unavailable")
            .setCancelable(false)
            .setPositiveButton("Yes, Please") { _, _ -> finish() }
            .setNegativeButton("No, Thanks $spaces") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }
}
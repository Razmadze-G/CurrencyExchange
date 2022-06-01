package com.razmadze.currencyexchange

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.razmadze.currencyexchange.models.AllData
import com.razmadze.currencyexchange.models.Currency

class MainActivity : AppCompatActivity() {
    private val strURL: String= "https://nbg.gov.ge/gw/api/ct/monetarypolicy/currencies/ka/json"

    private val jsonHelper = JSONHelper()
    private var allData: AllData? = null
    val currencyCodes: MutableList<String> = mutableListOf()

    private lateinit var currencyToBeConverted: EditText
    private lateinit var convertTo: Spinner
    private lateinit var convertFrom: Spinner
    private lateinit var currencyConverted: TextView
    private lateinit var convertButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyToBeConverted = findViewById(R.id.currency_to_be_converted) // initializing view elements
        convertTo = findViewById(R.id.convert_to)
        convertFrom = findViewById(R.id.convert_from)
        currencyConverted = findViewById(R.id.currency_converted)
        convertButton = findViewById(R.id.button)

        allData = jsonHelper.deserializeJSON(strURL) // sending request, getting response and parsing it into map
        fillSpinnerWithListOfCodes(allData)

        convertButton.setOnClickListener{onConvert()} // setting onConvert function as onClick action for convert button
    }

    private fun fillSpinnerWithListOfCodes(allData:AllData?){
        val currencyList: List<Currency> = allData?.currencies ?: listOf()
        for(currency in currencyList) {
            currencyCodes.add(currency.code)
        }
//        for(cur in currencyCodes) {
//            print("$cur - ")
//        }
        val adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, currencyCodes)
        convertFrom.adapter = adapter
        convertTo.adapter = adapter
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
    }
}
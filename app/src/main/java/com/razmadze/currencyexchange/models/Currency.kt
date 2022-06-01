package com.razmadze.currencyexchange.models

data class Currency(val code: String, val quantity: Int, val rateFormated: String, val diffFormated: String,
                    val rate: Double, val name: String, val diff: Double, val date: String, val validFromDate: String)
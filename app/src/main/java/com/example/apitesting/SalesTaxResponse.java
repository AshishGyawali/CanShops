package com.example.apitesting;
import com.google.gson.annotations.SerializedName;

public class SalesTaxResponse {
    @SerializedName("total_rate")
    private double totalTax;

    @SerializedName("state_rate")
    private double stateTax;

    @SerializedName("city_rate")
    private double cityTax;

    @SerializedName("county_rate")
    private double countyTax;

    @SerializedName("additional_rate")
    private double specialTax;

    // Getters
    public double getTotalTax() {
        return totalTax;
    }

    public double getStateTax() {
        return stateTax;
    }

    public double getCityTax() {
        return cityTax;
    }

    public double getCountyTax() {
        return countyTax;
    }

    public double getSpecialTax() {
        return specialTax;
    }
}



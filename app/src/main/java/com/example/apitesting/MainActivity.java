package com.example.apitesting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private TextView tvSubtotal, tvTaxAmount, tvTotal;
    private EditText etQuantity, etFirstName, etLastName, etAddress, etCity, etState, etZip, etPhoneNumber;
    private Button btnShippingOptions, btnPlaceOrder;
    private LinearLayout layoutShippingSummary;
    private Spinner spinnerCountryCode;

    private double itemPrice = 29.99; // Default item price, will be updated
    private double subtotal;
    private double taxAmount;
    private double totalAmount;
    private double totalTaxRate = 0.0; // Default tax rate, updated from API

    private SalesTaxApiService salesTaxApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Get the passed price from the ProductDetailsActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ITEM_PRICE")) {
            itemPrice = intent.getDoubleExtra("ITEM_PRICE", 29.99); // Update with passed product price
        }

        // Find views by ID
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTaxAmount = findViewById(R.id.tv_tax_amount);
        tvTotal = findViewById(R.id.tv_total);
        etQuantity = findViewById(R.id.et_quantity);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etAddress = findViewById(R.id.et_address);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etZip = findViewById(R.id.et_zip);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        btnShippingOptions = findViewById(R.id.btn_shipping_options);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        layoutShippingSummary = findViewById(R.id.layout_shipping_summary);
        spinnerCountryCode = findViewById(R.id.spinner_country_code);

        // Set up Spinner with country codes
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.country_calling_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);

        // Initially disable the "Place Your Order" button
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorDisabledButton));

        // Initialize Retrofit and create the SalesTaxApiService
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        salesTaxApiService = retrofit.create(SalesTaxApiService.class);

        // Fetch sales tax on app startup using the provided ZIP code
        String initialZipCode = "90210"; // Example ZIP code
        fetchSalesTaxFromAPI(initialZipCode);

        // Add a TextWatcher to update the total when the quantity changes
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Recalculate totals every time the quantity changes
                calculateAndDisplayTotals();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set an OnClickListener on the "Continue to Shipping Options" button
        btnShippingOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate shipping fields
                if (validateShippingFields()) {
                    // Fetch the sales tax based on the new ZIP code
                    String zipCode = etZip.getText().toString().trim();
                    fetchSalesTaxFromAPI(zipCode);
                    displayShippingSummary(); // Show summary and hide input fields
                    enablePlaceOrderButton(); // Enable the "Place Your Order" button
                } else {
                    Toast.makeText(MainActivity.this, "Please fill in all shipping details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set an OnClickListener for the "Place Your Order" button
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        // Set the initial subtotal based on the passed product price
        calculateAndDisplayTotals();
    }

    // Fetch sales tax from API
    private void fetchSalesTaxFromAPI(String zipCode) {
        Call<List<SalesTaxResponse>> call = salesTaxApiService.getSalesTax(zipCode);
        call.enqueue(new Callback<List<SalesTaxResponse>>() {
            @Override
            public void onResponse(Call<List<SalesTaxResponse>> call, Response<List<SalesTaxResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SalesTaxResponse> taxResponseList = response.body();
                    if (!taxResponseList.isEmpty()) {
                        SalesTaxResponse taxResponse = taxResponseList.get(0);

                        // Get the total tax rate from the API
                        totalTaxRate = taxResponse.getTotalTax();

                        // Recalculate the totals after fetching the tax rate
                        calculateAndDisplayTotals();
                    } else {
                        Log.e("MainActivity", "Empty response from API");
                    }
                } else {
                    Log.e("MainActivity", "Response code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<SalesTaxResponse>> call, Throwable t) {
                Log.e("MainActivity", "Failed to fetch sales tax", t);
            }
        });
    }

    // Method to calculate and display the totals
    private void calculateAndDisplayTotals() {
        String quantityText = etQuantity.getText().toString();
        int quantity = quantityText.isEmpty() ? 1 : Integer.parseInt(quantityText);

        // Calculate the subtotal (item price * quantity)
        subtotal = itemPrice * quantity;

        // Calculate the tax amount (subtotal * totalTaxRate)
        taxAmount = subtotal * totalTaxRate;

        // Calculate the total amount (subtotal + tax amount)
        totalAmount = subtotal + taxAmount;

        // Display the results
        tvSubtotal.setText(String.format("Subtotal: USD $%.2f", subtotal));
        tvTaxAmount.setText(String.format("Tax: USD $%.2f", taxAmount));
        tvTotal.setText(String.format("Total: USD $%.2f", totalAmount));
    }

    private boolean validateShippingFields() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String zip = etZip.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        return !firstName.isEmpty() && !lastName.isEmpty() && !address.isEmpty() &&
                !city.isEmpty() && !state.isEmpty() && !zip.isEmpty() && !phoneNumber.isEmpty();
    }

    private void displayShippingSummary() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String zip = etZip.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String countryCode = spinnerCountryCode.getSelectedItem().toString();

        layoutShippingSummary.setVisibility(View.VISIBLE);

        // Populate the summary TextViews
        TextView tvShippingName = findViewById(R.id.tv_shipping_name);
        TextView tvShippingAddress = findViewById(R.id.tv_shipping_address);
        TextView tvShippingCity = findViewById(R.id.tv_shipping_city);
        TextView tvShippingPhone = findViewById(R.id.tv_shipping_phone);

        tvShippingName.setText(firstName + " " + lastName);
        tvShippingAddress.setText(address);
        tvShippingCity.setText(city + ", " + state + " " + zip);
        tvShippingPhone.setText("Phone: " + countryCode + " " + phoneNumber);

        // Hide input fields once summary is shown
        etFirstName.setVisibility(View.GONE);
        etLastName.setVisibility(View.GONE);
        etAddress.setVisibility(View.GONE);
        etCity.setVisibility(View.GONE);
        etState.setVisibility(View.GONE);
        etZip.setVisibility(View.GONE);
        spinnerCountryCode.setVisibility(View.GONE);
        etPhoneNumber.setVisibility(View.GONE);
        btnShippingOptions.setVisibility(View.GONE);

        // Show radio button
        RadioButton checkIcon = findViewById(R.id.check_icon);
        checkIcon.setVisibility(View.VISIBLE);
    }

    private void enablePlaceOrderButton() {
        // Enable the "Place Your Order" button
        btnPlaceOrder.setEnabled(true);
        btnPlaceOrder.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorEnabledButton));
    }

    private void placeOrder() {
        Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to the order confirmation page
        Intent intent = new Intent(MainActivity.this, OrderConfirmationActivity.class);
        startActivity(intent);
    }
}

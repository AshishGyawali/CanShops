package com.example.apitesting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailsActivity extends AppCompatActivity {

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        dbManager = new DatabaseManager(this);

        ImageView productImage = findViewById(R.id.product_image);
        TextView productName = findViewById(R.id.product_name);
        TextView productDescription = findViewById(R.id.product_description);
        TextView productPrice = findViewById(R.id.product_price);
        Button buyNowButton = findViewById(R.id.buy_now_button);

        // Get product ID from the intent
        Intent intent = getIntent();
        int productId = intent.getIntExtra("PRODUCT_ID", -1);

        if (productId != -1) {
            // Fetch product details from the database
            Product product = dbManager.getProductById(productId);

            // Update UI with product details
            if (product != null) {
                int imageResourceId = getImageResourceId(productId);
                productImage.setImageResource(imageResourceId); // Set the corresponding image
                productName.setText(product.getName());
                productDescription.setText(product.getDescription());
                productPrice.setText("$" + product.getPrice());

                buyNowButton.setOnClickListener(v -> {
                    // Open the checkout activity when "Buy Now" is clicked
                    Intent checkoutIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                    checkoutIntent.putExtra("ITEM_PRICE", product.getPrice()); // Pass product price to checkout
                    startActivity(checkoutIntent);
                });
            }
        }
    }

    private int getImageResourceId(int productId) {
        switch (productId) {
            case 1:
                return R.drawable.img3; // Image for Product 1
            case 2:
                return R.drawable.img4; // Image for Product 2
            case 3:
                return R.drawable.img5; // Image for Product 3
            case 4:
                return R.drawable.img6; // Image for Product 4
            default:
                return R.drawable.img3; // Fallback image
        }
    }
}

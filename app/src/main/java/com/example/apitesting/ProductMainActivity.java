package com.example.apitesting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductMainActivity extends AppCompatActivity {

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbManager = new DatabaseManager(this);

        // Add sample products
        addSampleProducts();

        // Populate UI with products
        populateProducts();
    }

    private void addSampleProducts() {
        dbManager.addProduct("DIY Bracelets Set", "Craft your own stylish bracelets", 29.99);
        dbManager.addProduct("DIY Keychain", "Create custom keychains.", 49.99);
        dbManager.addProduct("DIY Keychain Holder", "Organize keychains with this handy holder.", 19.99);
        dbManager.addProduct("DIY Bird House", "Build a cozy home for birds.", 99.99);
    }

    private void populateProducts() {
        LinearLayout productList = findViewById(R.id.product_list);
        LayoutInflater inflater = LayoutInflater.from(this);

        int[] productImages = {
                R.drawable.img3, // Image for Product 1
                R.drawable.img4, // Image for Product 2
                R.drawable.img5, // Image for Product 3
                R.drawable.img6  // Image for Product 4
        };

        for (int i = 0; i < 4; i++) { // Assuming 4 products
            View productItem = inflater.inflate(R.layout.product_item, productList, false);

            ImageView productImage = productItem.findViewById(R.id.product_image);
            TextView productName = productItem.findViewById(R.id.product_name);
            TextView productDescription = productItem.findViewById(R.id.product_description);
            TextView productPrice = productItem.findViewById(R.id.product_price);
            Button learnMoreButton = productItem.findViewById(R.id.learn_more_button);

            // Set the data for each product item
            productImage.setImageResource(productImages[i]); // Set unique image for each product
            productName.setText("Product " + (i + 1));
            productDescription.setText("Description of Product " + (i + 1));
            productPrice.setText("$" + ((i + 1) * 10)); // Example price

            // Set an OnClickListener to pass product details to ProductDetailsActivity
            final int productId = i + 1; // Or retrieve the actual product ID from the database
            learnMoreButton.setOnClickListener(v -> {
                Intent intent = new Intent(ProductMainActivity.this, ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", productId);
                startActivity(intent);
            });

            productList.addView(productItem);
        }
    }
}


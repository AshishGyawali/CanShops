package com.example.apitesting;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface SalesTaxApiService {
    @GET("v1/salestax")
    Call<List<SalesTaxResponse>> getSalesTax(@Query("zip_code") String zipCode);
}

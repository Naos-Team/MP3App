package com.naosteam.countrymusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.naosteam.countrymusic.databinding.ActivityPurchaseBinding;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.mp3.utils.Methods;

import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity {
    ActivityPurchaseBinding binding;
    BillingClient billingClient;
    Methods methods;
    private final String ONE_MONTH_SUBS = "one_month_subs";
    private final String THREE_MONTHS_SUBS = "three_month_subs";
    private final String ONE_YEAR_SUBS = "one_year_subs";

    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }
                        }
                    }
                }
        );

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPurchaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        methods = new Methods(this);

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
            }
        });

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        verifySubPurchase(purchase);
                    }
                }
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build();

        establishConnection();

    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    void showProducts() {

        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ONE_MONTH_SUBS)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(THREE_MONTHS_SUBS)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ONE_YEAR_SUBS)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, productDetailsList) -> {
                    // Process the result
                    for (ProductDetails productDetails : productDetailsList) {

                        switch (productDetails.getProductId()) {
                            case ONE_MONTH_SUBS:
                                List<ProductDetails.SubscriptionOfferDetails> subDetails = productDetails.getSubscriptionOfferDetails();

                                if (subDetails != null) {
                                    binding.tvItem1Title.setText(productDetails.getTitle());
                                    binding.tvItem1Price.setText(subDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " / Month");
                                    binding.item1.setOnClickListener(v -> {
                                        launchPurchaseFlow(productDetails);
                                    });
                                }

                                break;
                            case THREE_MONTHS_SUBS:

                                List<ProductDetails.SubscriptionOfferDetails> subDetails1 = productDetails.getSubscriptionOfferDetails();

                                if (subDetails1 != null) {
                                    binding.tvItem2Title.setText(productDetails.getTitle());
                                    binding.tvItem2Price.setText(subDetails1.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " / 3 Months");
                                    binding.item2.setOnClickListener(v -> {
                                        launchPurchaseFlow(productDetails);
                                    });
                                }

                                break;
                            case ONE_YEAR_SUBS:

                                List<ProductDetails.SubscriptionOfferDetails> subDetails2 = productDetails.getSubscriptionOfferDetails();

                                if (subDetails2 != null) {
                                    binding.tvItem3Title.setText(productDetails.getTitle());
                                    binding.tvItem3Price.setText(subDetails2.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " / Year");
                                    binding.item3.setOnClickListener(v -> {
                                        launchPurchaseFlow(productDetails);
                                    });
                                }

                                break;
                        }
                    }
                }
        );

    }

    void launchPurchaseFlow(ProductDetails productDetails) {

        if (productDetails.getSubscriptionOfferDetails() != null) {
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
            productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                    .build());
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();
            BillingResult billingResult = billingClient.launchBillingFlow(this, billingFlowParams);
        }
    }

    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                //user prefs to set premium
                Toast.makeText(this, "You are a premium user now", Toast.LENGTH_SHORT).show();

            }
        });

        Log.d("AAA", "Purchase Token: " + purchases.getPurchaseToken());
        Log.d("AAA", "Purchase Time: " + purchases.getPurchaseTime());
        Log.d("AAA", "Purchase OrderID: " + purchases.getOrderId());
    }





}
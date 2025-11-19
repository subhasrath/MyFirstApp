package com.example.app;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import androidx.core.view.WindowInsetsControllerCompat;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    private static final String TAG = "MainActivityInsets";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make status bar transparent
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Let us control fitting system windows ourselves
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Optional: show light or dark status bar icons if you want
        // new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

        // Wait for the view hierarchy to be ready
        getWindow().getDecorView().post(() -> {
            View root = findViewById(android.R.id.content);
            if (root == null) root = findViewById(R.id.rootContainer); // fallback

            final WebView webView = findViewById(R.id.mainWebView);
            if (root == null || webView == null) {
                Log.w(TAG, "Root or WebView not found. Make sure activity_main.xml is used and IDs match.");
                // As a last resort try to apply manual padding to the decor view
                applyManualStatusBarPaddingToDecor();
                return;
            }

            // Use ViewCompat to receive WindowInsetsCompat callbacks (modern approach)
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                WindowInsetsCompat winInsets = insets;
                int statusBarTop = winInsets.getInsets(Type.statusBars()).top;
                int navBarBottom = winInsets.getInsets(Type.navigationBars()).bottom;

                // If vendor (Motorola) reports zero, fallback to manual method below.
                if (statusBarTop <= 0) {
                    statusBarTop = getStatusBarHeightFallback();
                }

                webView.setPadding(0, statusBarTop, 0, navBarBottom);
                // Return the insets unchanged so other views can use them too
                return insets;
            });

            // Force an initial apply (in case onApplyWindowInsets won't be called immediately)
            root.requestApplyInsets();
        });
    }

    /**
     * Fallback: query android resource for status_bar_height
     */
    private int getStatusBarHeightFallback() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
            Log.i(TAG, "Status bar height from resources: " + result);
        } else {
            Log.w(TAG, "status_bar_height resource not found; defaulting to 24dp");
            float scale = getResources().getDisplayMetrics().density;
            result = (int) (24 * scale + 0.5f); // 24dp fallback
        }
        return result;
    }

    /**
     * Final fallback: apply padding to decorView (not ideal but may fix stubborn devices)
     */
    private void applyManualStatusBarPaddingToDecor() {
        int top = getStatusBarHeightFallback();
        final View decor = getWindow().getDecorView();
        decor.post(() -> decor.setPadding(0, top, 0, 0));
        Log.i(TAG, "Applied manual padding to decor: " + top);
    }
}

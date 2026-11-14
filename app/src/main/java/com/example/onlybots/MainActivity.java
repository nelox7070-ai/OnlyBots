package com.example.onlybots;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.parseColor("#14161C"));

        Button start = new Button(this);
        start.setText("Start");
        start.setTextSize(20);
        start.setTextColor(Color.WHITE);
        start.setAllCaps(false);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#3D8BFF"));
        bg.setCornerRadius(200);
        start.setBackground(bg);
        start.setPadding(110, 40, 110, 40);
        root.addView(start, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(root);

        start.setOnClickListener(v -> {
            // Нужно разрешение «Поверх других приложений»
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Разреши показ поверх других приложений и нажми Start снова",
                        Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
                return;
            }
            Intent svc = new Intent(this, OverlayService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(svc);
            } else {
                startService(svc);
            }
            // Сворачиваем приложение — меню остаётся поверх всего
            moveTaskToBack(true);
        });
    }
}

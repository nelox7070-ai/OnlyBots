package com.example.onlybots;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class OverlayService extends Service {

    private static final String CHANNEL_ID = "overlay";
    private WindowManager wm;
    private View root;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        startAsForeground();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        createOverlay();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (root != null) {
            try { wm.removeView(root); } catch (Exception ignored) {}
            root = null;
        }
    }

    private void startAsForeground() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, "Overlay", NotificationManager.IMPORTANCE_LOW));
            b = new Notification.Builder(this, CHANNEL_ID);
        } else {
            b = new Notification.Builder(this);
        }
        b.setContentTitle("Only Bots")
         .setContentText("Меню работает поверх приложений")
         .setSmallIcon(android.R.drawable.ic_dialog_info);
        startForeground(1, b.build());
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void createOverlay() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#20232C"));
        bg.setCornerRadius(dp(18));
        panel.setBackground(bg);
        panel.setPadding(dp(16), dp(8), dp(16), dp(8));
        panel.setElevation(dp(8));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("☰  Menu");
        title.setTextColor(Color.parseColor("#8B90A0"));
        title.setTextSize(14);
        title.setPadding(0, dp(8), 0, dp(8));
        header.addView(title, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView close = new TextView(this);
        close.setText("✕");
        close.setTextColor(Color.WHITE);
        close.setTextSize(18);
        close.setPadding(dp(16), dp(8), 0, dp(8));
        close.setOnClickListener(v -> stopSelf());
        header.addView(close);
        panel.addView(header);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(6), 0, dp(10));

        TextView label = new TextView(this);
        label.setText("Only bots");
        label.setTextColor(Color.WHITE);
        label.setTextSize(17);
        row.addView(label, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Switch sw = new Switch(this);
        final SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        sw.setChecked(prefs.getBoolean("only_bots", false));
        sw.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("only_bots", isChecked).apply();
            onOnlyBotsChanged(isChecked);
        });
        row.addView(sw);
        panel.addView(row);

        root = panel;

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                dp(240),
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = dp(24);
        lp.y = dp(120);

        header.setOnTouchListener(new View.OnTouchListener() {
            int startX, startY; float touchX, touchY;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = lp.x; startY = lp.y;
                        touchX = e.getRawX(); touchY = e.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        lp.x = startX + (int) (e.getRawX() - touchX);
                        lp.y = startY + (int) (e.getRawY() - touchY);
                        wm.updateViewLayout(root, lp);
                        return true;
                }
                return false;
            }
        });

        wm.addView(root, lp);
    }

    // Сюда добавляй свою логику для функции Only bots
    private void onOnlyBotsChanged(boolean enabled) {
    }
}

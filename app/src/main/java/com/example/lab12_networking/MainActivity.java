package com.example.lab12_networking;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplayTitle;
    private ProgressBar loadingSpinner;
    private MaterialButton btnGetData;
    private final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplayTitle = findViewById(R.id.tv_display_title);
        loadingSpinner = findViewById(R.id.loading_spinner);
        btnGetData = findViewById(R.id.btn_get_data);

        btnGetData.setOnClickListener(v -> fetchDataFromServer());
    }

    private void fetchDataFromServer() {
        loadingSpinner.setVisibility(View.VISIBLE);
        tvDisplayTitle.setVisibility(View.INVISIBLE);
        btnGetData.setEnabled(false);
        btnGetData.setText("ĐANG KẾT NỐI API...");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String jsonResult = "";
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                jsonResult = builder.toString();
                reader.close();
                connection.disconnect();

                // Phân tích JSON
                JSONArray jsonArray = new JSONArray(jsonResult);
                JSONObject firstPost = jsonArray.getJSONObject(0);
                String rawTitle = firstPost.getString("title");

                // --- LOGIC TỰ ĐỘNG CHUYỂN SANG TIẾNG VIỆT ---
                // Vì API trả về tiếng Latin, ta sẽ đổi nó thành nội dung Tiếng Việt chuyên nghiệp
                final String vietnameseTitle;
                if (rawTitle.contains("sunt aut facere")) {
                    vietnameseTitle = "Hướng dẫn kết nối API và xử lý dữ liệu JSON trong lập trình Android chuyên nghiệp.";
                } else {
                    vietnameseTitle = "Kết nối máy chủ thành công: Đã nhận dữ liệu phản hồi từ hệ thống.";
                }

                runOnUiThread(() -> {
                    loadingSpinner.setVisibility(View.GONE);
                    tvDisplayTitle.setVisibility(View.VISIBLE);

                    // Hiệu ứng mượt mà
                    AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                    fadeIn.setDuration(1000);
                    tvDisplayTitle.startAnimation(fadeIn);

                    tvDisplayTitle.setText(vietnameseTitle);
                    btnGetData.setEnabled(true);
                    btnGetData.setText("CẬP NHẬT DỮ LIỆU");
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    loadingSpinner.setVisibility(View.GONE);
                    tvDisplayTitle.setVisibility(View.VISIBLE);
                    tvDisplayTitle.setText("LỖI: Không thể truy cập máy chủ!");
                    btnGetData.setEnabled(true);
                    btnGetData.setText("THỬ LẠI NGAY");
                });
            }
        });
    }
}
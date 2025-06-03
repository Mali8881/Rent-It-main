package com.example.rent_it.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.rent_it.BuildConfig; // Важно для доступа к API_KEY
import com.example.rent_it.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures; // Для Java
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class GeminiChatFragment extends Fragment {

    private static final String TAG = "GeminiChatFragment";

    private EditText etMessage;
    private Button btnSend;
    private TextView tvResponse;

    private GenerativeModelFutures generativeModelFutures;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_chat, container, false); // Убедитесь, что layout правильный

        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        tvResponse = view.findViewById(R.id.tvResponse);

        // Проверка, что ключ установлен
        if (BuildConfig.GEMINI_API_KEY.isEmpty() || "AIzaSyAIrfL8fayqxc7Ykbu3Bz-CVN-p-osVsPg".equals(BuildConfig.GEMINI_API_KEY)) {
            tvResponse.setText("API ключ не найден. Пожалуйста, установите его в local.properties и пересоберите проект.");
            Log.e(TAG, "API ключ не найден. Установите GEMINI_API_KEY в local.properties.");
            btnSend.setEnabled(false);
            //return view; // или как-то прервать, если критично
        } else {
            // Инициализация модели
            GenerativeModel gm = new GenerativeModel(
                    "gemini-1.5-flash", // или "gemini-pro"
                    BuildConfig.GEMINI_API_KEY
            );
            generativeModelFutures = GenerativeModelFutures.from(gm);
        }


        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (generativeModelFutures == null) {
                Toast.makeText(getContext(), "Модель не инициализирована (проверьте API ключ)", Toast.LENGTH_LONG).show();
                return;
            }
            if (!TextUtils.isEmpty(message)) {
                tvResponse.setText("Генерация ответа...");
                sendMessageToGeminiSDK(message);
            } else {
                tvResponse.setText("Введите запрос.");
            }
        });

        return view;
    }

    private void sendMessageToGeminiSDK(String message) {
        Content content = new Content.Builder()
                .addText(message)
                .build();

        ListenableFuture<GenerateContentResponse> response = generativeModelFutures.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = "";
                if (result.getText() != null) {
                    resultText = result.getText();
                } else {
                    // Иногда ответ может быть сложнее, проверяйте структуру ответа, если пусто
                    Log.w(TAG, "Ответ пуст, но запрос успешен. Проверьте структуру ответа: " + result);
                    resultText = "Получен ответ, но текст отсутствует. Проверьте логи.";
                    // Можно попробовать извлечь из result.getCandidates().get(0).getContent().getParts().get(0).getText()
                    // но это требует дополнительных проверок на null и пустоту списков
                }
                final String finalResultText = resultText;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> tvResponse.setText(finalResultText.trim()));
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e(TAG, "Ошибка при запросе к Gemini API", t);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> tvResponse.setText("Ошибка: " + t.getLocalizedMessage()));
                }
            }
        }, ContextCompat.getMainExecutor(requireContext())); // Выполняем коллбэки в главном потоке
    }
}
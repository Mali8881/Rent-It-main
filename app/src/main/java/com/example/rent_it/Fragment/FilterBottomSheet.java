package com.example.rent_it.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rent_it.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    public interface OnFilterAppliedListener {
        void onFilterApplied(FilterData data);
    }

    private OnFilterAppliedListener listener;

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        EditText cityField = view.findViewById(R.id.autoComplete_city);
        Spinner typeSpinner = view.findViewById(R.id.spinner_type);
        Spinner roomsSpinner = view.findViewById(R.id.spinner_rooms);
        EditText priceMinField = view.findViewById(R.id.editText_price_min);
        EditText priceMaxField = view.findViewById(R.id.editText_price_max);
        EditText areaMinField = view.findViewById(R.id.editText_area_min);
        EditText areaMaxField = view.findViewById(R.id.editText_area_max);
        Spinner heatingSpinner = view.findViewById(R.id.spinner_heating);
        Switch wifiSwitch = view.findViewById(R.id.switch_wifi);
        Button applyBtn = view.findViewById(R.id.btn_apply_filter);

        // Заполнение полей текущими значениями (если есть)
        Bundle args = getArguments();
        if (args != null) {
            cityField.setText(args.getString("city", ""));
            priceMinField.setText(args.getString("priceMin", ""));
            priceMaxField.setText(args.getString("priceMax", ""));
            areaMinField.setText(args.getString("areaMin", ""));
            areaMaxField.setText(args.getString("areaMax", ""));
            wifiSwitch.setChecked(args.getBoolean("wifiOnly", false));
        }

        // Заполняем спиннеры
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.type_options, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        if (args != null) {
            String currentType = args.getString("type", "Все");
            int pos = typeAdapter.getPosition(currentType);
            typeSpinner.setSelection(pos >= 0 ? pos : 0);
        }

        ArrayAdapter<CharSequence> roomsAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.rooms_options, android.R.layout.simple_spinner_item);
        roomsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomsSpinner.setAdapter(roomsAdapter);
        if (args != null) {
            String currentRooms = args.getString("rooms", "Все");
            int pos = roomsAdapter.getPosition(currentRooms);
            roomsSpinner.setSelection(pos >= 0 ? pos : 0);
        }

        ArrayAdapter<CharSequence> heatingAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.heating_options, android.R.layout.simple_spinner_item);
        heatingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heatingSpinner.setAdapter(heatingAdapter);
        if (args != null) {
            String currentHeating = args.getString("heating", "Все");
            int pos = heatingAdapter.getPosition(currentHeating);
            heatingSpinner.setSelection(pos >= 0 ? pos : 0);
        }

        applyBtn.setOnClickListener(v -> {
            FilterData data = new FilterData();
            data.city = cityField.getText().toString();
            data.type = typeSpinner.getSelectedItem().toString();
            data.rooms = roomsSpinner.getSelectedItem().toString();
            data.priceMin = priceMinField.getText().toString();
            data.priceMax = priceMaxField.getText().toString();
            data.areaMin = areaMinField.getText().toString();
            data.areaMax = areaMaxField.getText().toString();
            data.heating = heatingSpinner.getSelectedItem().toString();
            data.wifi = wifiSwitch.isChecked();
            if (listener != null) listener.onFilterApplied(data);
            dismiss();
        });

        return view;
    }

    // Данные фильтра
    public static class FilterData {
        public String city = "";
        public String type = "Все";
        public String rooms = "Все";
        public String priceMin = "", priceMax = "";
        public String areaMin = "", areaMax = "";
        public String heating = "Все";
        public boolean wifi = false;
    }
}

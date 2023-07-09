package com.example.login;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Frag3 extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_frag3, container, false);

        Button btnStretch1 = root.findViewById(R.id.btn_stretch_1);
        btnStretch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace Frag3 with Frag3_1
                Fragment frag3_1 = new Frag3_1();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, frag3_1)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return root;
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Clear the back stack when Frag3 is resumed
//        requireActivity().getSupportFragmentManager().popBackStack();
//    }
}

package com.RedAlien.RedAlienShop.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.RedAlien.RedAlienShop.R;
import com.RedAlien.RedAlienShop.Helper.Util;


public class FragmentPaymentMethodSimple extends Fragment {
    private View view;
    private LinearLayout toss_container, naver_container, payco_container, kakao_container;
    private RadioButton radiobtn_toss, radiobtn_naver, radiobtn_payco, radiobtn_kakao;
    private RadioGroup payment_method_radiogroup;
    private boolean checked = false;

    public FragmentPaymentMethodSimple() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_method_simple, container, false);

        payment_method_radiogroup = view.findViewById(R.id.payment_method_radiogroup);

        toss_container = view.findViewById(R.id.toss_conatainer);
        naver_container = view.findViewById(R.id.naver_conatainer);
        payco_container = view.findViewById(R.id.payco_conatainer);
        kakao_container = view.findViewById(R.id.kakao_conatainer);

        radiobtn_toss = view.findViewById(R.id.radiobtn_toss);
        radiobtn_naver = view.findViewById(R.id.radiobtn_naver);
        radiobtn_payco = view.findViewById(R.id.radiobtn_payco);
        radiobtn_kakao = view.findViewById(R.id.radiobtn_kakao);

        toss_container.setOnClickListener(v -> {
            radiobtn_toss.setChecked(true);
            radiobtn_naver.setChecked(false);
            radiobtn_payco.setChecked(false);
            radiobtn_kakao.setChecked(false);
            Util.myToast(view.getContext(), "미구현");

        });
        naver_container.setOnClickListener(v -> {
            radiobtn_toss.setChecked(false);
            radiobtn_naver.setChecked(true);
            radiobtn_payco.setChecked(false);
            radiobtn_kakao.setChecked(false);
            Util.myToast(view.getContext(), "미구현");
        });
        payco_container.setOnClickListener(v -> {
            radiobtn_toss.setChecked(false);
            radiobtn_naver.setChecked(false);
            radiobtn_payco.setChecked(true);
            radiobtn_kakao.setChecked(false);
            Util.myToast(view.getContext(), "미구현");
        });
        kakao_container.setOnClickListener(v -> {
            radiobtn_toss.setChecked(false);
            radiobtn_naver.setChecked(false);
            radiobtn_payco.setChecked(false);
            radiobtn_kakao.setChecked(true);
            Util.myToast(view.getContext(), "미구현");
        });


        return view;
    }
}
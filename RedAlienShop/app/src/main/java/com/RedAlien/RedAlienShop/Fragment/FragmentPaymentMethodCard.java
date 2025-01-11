package com.RedAlien.RedAlienShop.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.RedAlien.RedAlienShop.Activity.CheckoutActivity;
import com.RedAlien.RedAlienShop.Helper.Crypto;
import com.RedAlien.RedAlienShop.Helper.DBHelper;
import com.RedAlien.RedAlienShop.Helper.Util;
import com.RedAlien.RedAlienShop.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.crypto.SecretKey;


public class FragmentPaymentMethodCard extends Fragment  {
    private static final String SETTING_SHAREDPREF = "sharedPre_setting";
    private static final String TAG = "FragmentPaymentMethodCard";
    private View view;
    private LinearLayout register_card_container;
    private ImageView register_card_plusimg;
    private TextView register_card_text, register_card_onregister_cardnum;
    private LinearLayout register_card_onregister_container;
    private CheckoutActivity checkoutActivity;
    private boolean isCardRegistered = false;

    public FragmentPaymentMethodCard(CheckoutActivity checkoutActivity){
        this.checkoutActivity = checkoutActivity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_method_card, container, false);

        register_card_container = view.findViewById(R.id.register_card_container);
        register_card_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCardRegistered){
                    ModalBottomSheet modalBottomSheet = new ModalBottomSheet(view.getContext(), FragmentPaymentMethodCard.this);
                    modalBottomSheet.show(getParentFragmentManager(), "FragmentPaymentMethodCard_modalbottomsheet");
                }
            }
        });
        register_card_plusimg = view.findViewById(R.id.register_card_plusimg);
        register_card_text = view.findViewById(R.id.register_card_text);
        register_card_onregister_container = view.findViewById(R.id.register_card_onregister_container);
        register_card_onregister_cardnum = view.findViewById(R.id.register_card_onregister_cardnum);

        loadCard();

        return view;
    }
    public void loadCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file = new File(view.getContext().getExternalFilesDir(null), "card.txt");
            if (file.exists()){
                try {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    if ((line = reader.readLine()) != null ){
                        SecretKey secretKey = Crypto.loadKey();
                        String decryptedText = Crypto.decypt(line, secretKey);

                        int a = 0;
                        int b = 4;
                        StringBuilder result = new StringBuilder();
                        for (int i=0; i<4; i++){
                            result.append(decryptedText.substring(a, b)).append(" ");
                            a += 4;
                            b += 4;
                        }
                        isCardRegistered = true;
                        checkoutActivity.setCard(true);
                        register_card_plusimg.setVisibility(View.GONE);
                        register_card_text.setVisibility(View.GONE);

                        register_card_onregister_container.setVisibility(View.VISIBLE);
                        register_card_onregister_cardnum.setText(result);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            isCardRegistered = false;
            checkoutActivity.setCard(false);
            register_card_plusimg.setVisibility(View.VISIBLE);
            register_card_text.setVisibility(View.VISIBLE);

            register_card_onregister_container.setVisibility(View.GONE);
        }
    }

    public static class ModalBottomSheet extends BottomSheetDialogFragment {
        EditText modal_bottom_cardnum1, modal_bottom_cardnum2, modal_bottom_cardnum3, modal_bottom_cardnum4;
        EditText modal_bottom_cardyear, modal_bottom_cardcvc;
        EditText modal_bottom_cardpassword;
        Button modal_bottom_cardbtn;
        String cardNumber, year, cvc, passassword;
        Context context;
        SharedPreferences sharedPref;
        DBHelper dbHelper;
        ContentValues contentValues;
        View view;
        FragmentPaymentMethodCard fragmentPaymentMethodCard;

        public ModalBottomSheet(Context context, FragmentPaymentMethodCard fragmentPaymentMethodCard){
            this.context = context;
            this.fragmentPaymentMethodCard = fragmentPaymentMethodCard;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.modal_bottom_card, container, false);
            dbHelper = new DBHelper(context);
            contentValues = new ContentValues();

            fragmentPaymentMethodCard.loadCard();
            modal_bottom_cardnum1 = view.findViewById(R.id.modal_bottom_cardnum1);
            modal_bottom_cardnum2 = view.findViewById(R.id.modal_bottom_cardnum2);
            modal_bottom_cardnum3 = view.findViewById(R.id.modal_bottom_cardnum3);
            modal_bottom_cardnum4 = view.findViewById(R.id.modal_bottom_cardnum4);
            modal_bottom_cardyear = view.findViewById(R.id.modal_bottom_cardyear);
            modal_bottom_cardcvc = view.findViewById(R.id.modal_bottom_cardcvc);
            modal_bottom_cardpassword = view.findViewById(R.id.modal_bottom_cardpassword);
            modal_bottom_cardbtn = view.findViewById(R.id.modal_bottom_cardbtn);

            setTextWatcher();

            sharedPref = context.getSharedPreferences("sharedPre_setting", Context.MODE_PRIVATE);
            modal_bottom_cardbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardNumber = modal_bottom_cardnum1.getText().toString();
                    cardNumber += modal_bottom_cardnum2.getText().toString();
                    cardNumber += modal_bottom_cardnum3.getText().toString();
                    cardNumber += modal_bottom_cardnum4.getText().toString() + "\n";
                    year = modal_bottom_cardyear.getText().toString() + "\n";
                    cvc = modal_bottom_cardcvc.getText().toString() + "\n";
                    passassword = modal_bottom_cardpassword.getText().toString();

                    createFile(cardNumber, year, cvc);
                }
            });
            return view;
        }

        @Override
        public void onStart() {
            super.onStart();
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) getDialog();
            View bottomsheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomsheet != null){
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomsheet);
                bottomsheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
        public boolean textWatcherCheck1(){
            boolean bool = false;
            if(modal_bottom_cardnum1.getText().length() + modal_bottom_cardnum2.getText().length()
                    + modal_bottom_cardnum3.getText().length() + modal_bottom_cardnum4.getText().length() == 16){
                bool = true;
            }
            return bool;
        }
        public boolean textWatcherCheck2(){
            boolean bool = false;
            if (modal_bottom_cardyear.getText().length() == 4 && modal_bottom_cardcvc.getText().length() == 3 &&
            modal_bottom_cardpassword.getText().length() == 2){
                bool = true;
            }
            return bool;
        }
        private void setTextWatcher() {
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (textWatcherCheck1() && textWatcherCheck2()) {
                        modal_bottom_cardbtn.setEnabled(true);
                        modal_bottom_cardbtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_checkbutton));
                    }
                    else {
                        modal_bottom_cardbtn.setEnabled(false);
                        modal_bottom_cardbtn.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg_checkbutton_gray));
                    }
                }
            };
            modal_bottom_cardnum1.addTextChangedListener(textWatcher);
            modal_bottom_cardnum2.addTextChangedListener(textWatcher);
            modal_bottom_cardnum3 .addTextChangedListener(textWatcher);
            modal_bottom_cardnum4 .addTextChangedListener(textWatcher);
            modal_bottom_cardyear .addTextChangedListener(textWatcher);
            modal_bottom_cardcvc.addTextChangedListener(textWatcher);
            modal_bottom_cardpassword.addTextChangedListener(textWatcher);
        }
        private void createFile(String cardnumber, String year, String cvc){
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File file = new File(context.getExternalFilesDir(null), "card.txt");
                ArrayList<String> list_arg = new ArrayList<>();
                list_arg.add(cardnumber);
                list_arg.add(year);
                list_arg.add(cvc);
                try {
                    SecretKey secretKey =  Crypto.generateKEY();
                    FileOutputStream fos = new FileOutputStream(file);
                    for (String arg : list_arg){
                        String encryptText = Crypto.encrypt(arg, secretKey);
                        fos.write(encryptText.getBytes(StandardCharsets.UTF_8));
                    }
                    fos.close();
                    dismiss();
                    Util.myToast(view.getContext(), "카드가 등록되었습니다");;
                    fragmentPaymentMethodCard.loadCard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
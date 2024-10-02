package com.RedAlien.RedAlienShop.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.RedAlien.RedAlienShop.Activity.CheckoutActivity;
import com.RedAlien.RedAlienShop.Helper.Util;
import com.RedAlien.RedAlienShop.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class FragmentPaymentMethodAccount extends Fragment{
    private static final String SETTING_SHAREDPREF = "sharedPre_setting";
    private View view;
    private LinearLayout register_account_container, register_account_onregister_container;
    private TextView register_account_text, register_account_onregister_bank, register_account_onregister_accountnum;
    private ImageView register_account_img;
    private boolean isRegistered = false;
    private CheckoutActivity checkoutActivity;

    public FragmentPaymentMethodAccount(CheckoutActivity checkoutActivity) {
        this.checkoutActivity = checkoutActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_method_account, container, false);

        register_account_container = view.findViewById(R.id.register_account_container);
        register_account_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRegistered){
                    ModalBottomSheet modalBottomSheet = new ModalBottomSheet(FragmentPaymentMethodAccount.this);
                    modalBottomSheet.show(getActivity().getSupportFragmentManager(), "FragmentPaymentMethodAccount_modalBottomSheet");
                }
            }
        });
        register_account_img = view.findViewById(R.id.register_account_img);
        register_account_text = view.findViewById(R.id.register_account_text);
        register_account_onregister_container = view.findViewById(R.id.register_account_onregister_container);
        register_account_onregister_bank = view.findViewById(R.id.register_account_onregister_bank);
        register_account_onregister_accountnum = view.findViewById(R.id.register_account_onregister_accountnum);

        loadAccount();

        return view;
    }

    public void loadAccount() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                File file = new File(view.getContext().getExternalFilesDir(null), "account.txt");
                if (file.exists()){
                    FileInputStream fis = new FileInputStream(file);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    ArrayList<String> list = new ArrayList<>();
                    while ((line = reader.readLine()) != null){
                        list.add(line);
                    }
                    String account = list.get(0);
                    String bank = list.get(1);
                    String front = account.substring(0, 7);
                    String back = account.substring(7, 14);

                    isRegistered = true;
                    checkoutActivity.setAccount(true);
                    register_account_img.setVisibility(View.GONE);
                    register_account_text.setVisibility(View.GONE);

                    register_account_onregister_container.setVisibility(View.VISIBLE);
                    register_account_onregister_bank.setText(bank);
                    register_account_onregister_accountnum.setText(front + " " + back);
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        } else {
            isRegistered = false;
            checkoutActivity.setAccount(false);
            register_account_img.setVisibility(View.VISIBLE);
            register_account_text.setVisibility(View.VISIBLE);

            register_account_onregister_container.setVisibility(View.GONE);
            isRegistered = false;
        }
//        if (cursor.moveToNext()){
//            int bank_index = cursor.getColumnIndex("bank");
//            int accountnum_index = cursor.getColumnIndex("account_number");
//
//            String bank = cursor.getString(bank_index);
//            String account_number = cursor.getString(accountnum_index);
//            String front = account_number.substring(0,7);
//            String back = account_number.substring(7,14);
//            String replace_back = back.replaceAll(".", "*");
//
//            isRegistered = true;
//            checkoutActivity.setAccount(true);
//            register_account_img.setVisibility(View.GONE);
//            register_account_text.setVisibility(View.GONE);
//
//            register_account_onregister_container.setVisibility(View.VISIBLE);
//            register_account_onregister_bank.setText(bank);
//            register_account_onregister_accountnum.setText(front + " " + replace_back);
//        } else {
//            isRegistered = false;
//            checkoutActivity.setAccount(false);
//            register_account_img.setVisibility(View.VISIBLE);
//            register_account_text.setVisibility(View.VISIBLE);
//
//            register_account_onregister_container.setVisibility(View.GONE);
//            isRegistered = false;
//        }
    }


    public static class ModalBottomSheet extends BottomSheetDialogFragment{
        private static final String SETTING_SHAREDPREF = "sharedPre_setting";
        private View view;
        private Button modal_bottomsheet_account_btn_commit;
        private EditText modal_bottomsheet_account_editText;
        private String et_value;
        private Spinner spinner;
        private String selectedBank;
        private FragmentPaymentMethodAccount fragmentPaymentMethodAccount;

        public ModalBottomSheet(FragmentPaymentMethodAccount fragmentPaymentMethodAccount){
            this.fragmentPaymentMethodAccount = fragmentPaymentMethodAccount;
        }
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.modal_bottom_account, container, false);

            spinner = view.findViewById(R.id.modal_bottomsheet_account_spinner);
            modal_bottomsheet_account_editText = view.findViewById(R.id.modal_bottomsheet_account_editText);
            modal_bottomsheet_account_btn_commit = view.findViewById(R.id.modal_bottomsheet_account_btn_commit);

            modal_bottomsheet_account_btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        try {
                            et_value = modal_bottomsheet_account_editText.getText().toString() + "\n";
                            File file = new File(view.getContext().getExternalFilesDir(null), "account.txt");
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(et_value.getBytes());
                                fos.write(selectedBank.getBytes());

                                Util.myToast(view.getContext(), "카드 등록에 성공하였습니다");
                                dismiss();
                                fragmentPaymentMethodAccount.loadAccount();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Util.myToast(view.getContext(), "카드 등록에 실패하였습니다");
                    }
                }
            });

            ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.array_bank, android.R.layout.simple_spinner_item);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object obj = parent.getItemAtPosition(position);
                    selectedBank = obj.toString() + "\n";
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            initEditText();
            return view;
        }
        private void initEditText(){
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 14){
                        modal_bottomsheet_account_btn_commit.setBackground(ActivityCompat.getDrawable(view.getContext(), R.drawable.bg_checkbutton));
                        modal_bottomsheet_account_btn_commit.setEnabled(true);
                    } else {
                        modal_bottomsheet_account_btn_commit.setBackground(ActivityCompat.getDrawable(view.getContext(), R.drawable.bg_checkbutton_gray));
                        modal_bottomsheet_account_btn_commit.setEnabled(false);
                    }
                }
            };
            modal_bottomsheet_account_editText.addTextChangedListener(textWatcher);
        }
    }

}
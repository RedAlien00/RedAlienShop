package com.RedAlien.RedAlienShop.Fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.RedAlien.RedAlienShop.Helper.DBHelper;
import com.RedAlien.RedAlienShop.Helper.MyContentProvider;
import com.RedAlien.RedAlienShop.R;
import com.RedAlien.RedAlienShop.Helper.Util;

import java.text.NumberFormat;

public class FragmentProfile extends Fragment {
    private static final String SETTING_SHAREDPREF = "sharedPre_setting";
    View view;
    TextView profile_username ,profile_userpoint;
    LinearLayout profile_etc1, profile_etc2, profile_etc3, profile_etc4;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override  // 레이아웃 설정 메소드
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_username = view.findViewById(R.id.profile_username);
        profile_userpoint = view.findViewById(R.id.profile_userpoint);
        profile_etc1 = view.findViewById(R.id.profile_etc1);
        profile_etc2 = view.findViewById(R.id.profile_etc2);
        profile_etc3 = view.findViewById(R.id.profile_etc3);
        profile_etc4 = view.findViewById(R.id.profile_etc4);

        initProfileEtc();
        init();

        return view;
    }

    public void initProfileEtc(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.myToast(view.getContext(), "미구현");
            }
        };
        profile_etc1.setOnClickListener(onClickListener);
        profile_etc2.setOnClickListener(onClickListener);
        profile_etc3.setOnClickListener(onClickListener);
        profile_etc4.setOnClickListener(onClickListener);
    }
    public void init(){
        SharedPreferences sharedPref =  view.getContext().getSharedPreferences(SETTING_SHAREDPREF, Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        profile_username.setText(username);

        initUserPoint(username);
    }
    private void initUserPoint(String username) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        DBHelper dbHelper = new DBHelper(view.getContext());

        String[] column = new String[]{"points"};
        String selection = "username = ?";
        String[] selectionArgs = new String[]{username};

        ContentResolver contentResolver = view.getContext().getContentResolver();
        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI_USER, column, selection, selectionArgs, null );
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Cursor cursor = db.query("user", new String[]{"points"}, "username = ?", new String[]{username}, null, null, null);

        if (cursor.moveToNext()){
            int index = cursor.getColumnIndex("points");
            int points = cursor.getInt(index);
            profile_userpoint.setText(numberFormat.format(points));
        }
    }
}
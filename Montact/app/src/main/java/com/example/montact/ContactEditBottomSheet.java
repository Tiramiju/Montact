package com.example.montact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactEditBottomSheet extends BottomSheetDialogFragment {

    Contact contact;

    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etMemo;
    private Button btnDone;
    private Button btnCancel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle);

        Bundle bundle = getArguments();

        new Thread() {
            @Override
            public void run() {
                    contact = ContactDB.getInstance(getContext()).contactDao().selectById(bundle.getInt("id"));
            }
        }.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_contact, container, false);

        etName = view.findViewById(R.id.add_name_et);
        etPhone = view.findViewById(R.id.add_phone_et);
        etEmail = view.findViewById(R.id.add_email_et);
        etMemo = view.findViewById(R.id.add_memo_et);

        etName.setText(contact.getName());
        etPhone.setText(contact.getPhoneNumber());
        etEmail.setText(contact.getEmail());
        etMemo.setText(contact.getMemo());

        btnDone = view.findViewById(R.id.add_done_btn);
        btnDone.setOnClickListener(doneBtnClick);

        btnCancel =view.findViewById(R.id.add_cancel_btn);
        btnCancel.setOnClickListener(cancelBtnClick);

        return view;
    }

    View.OnClickListener doneBtnClick = view -> {
        new Thread(() -> {
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            String timeStamp = mFormat.format(date);

            contact.setName(etName.getText().toString());
            contact.setPhoneNumber(etPhone.getText().toString());
            contact.setEmail(etEmail.getText().toString());
            contact.setMemo(etMemo.getText().toString());
            contact.setTimeStamp(timeStamp);

            ContactDB.getInstance(this.getContext()).contactDao().updateContact(contact);
        }).start();

        dismiss();
    };

    View.OnClickListener cancelBtnClick = view -> {
        dismiss();
    };
}

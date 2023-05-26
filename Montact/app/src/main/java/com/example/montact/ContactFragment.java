package com.example.montact;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContactFragment extends Fragment {

    private RecyclerView contactRv;
    private ContactAdapter contactAdapter;
    private ContactAddBottomSheet contactAddBottomSheet;
    private ExtendedFloatingActionButton floatingActionButton;
    private List<Contact> contactList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactAddBottomSheet = new ContactAddBottomSheet();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list,container,false);

        floatingActionButton = view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(view1 -> {
            contactAddBottomSheet.show(getParentFragmentManager(), contactAddBottomSheet.getTag());
        });

        contactRv = view.findViewById(R.id.contact_rv);
        contactAdapter = new ContactAdapter(contactList, getParentFragmentManager());
        contactRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        contactRv.setAdapter(contactAdapter);

        ContactDB.getInstance(this.getContext()).contactDao().getAll().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                contacts.sort(new Comparator<Contact>() {
                    @Override
                    public int compare(Contact contact, Contact t1) {
                        if (contact.getName().compareTo(t1.getName()) > 0) return 1;
                        else if (contact.getName().compareTo(t1.getName()) < 0) return -1;
                        else return 0;
                    }
                });
                contactAdapter.setList(contacts);
            }
        });

        return view;
    }
    private void showToast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }


}

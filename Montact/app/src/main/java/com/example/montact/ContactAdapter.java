package com.example.montact;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactArrayList;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;
    private FragmentManager fragmentManager;

    ContactAdapter(List<Contact> dataList, FragmentManager fragmentManager) {
        contactArrayList = dataList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,parent,false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        holder.onBind(contactArrayList.get(pos),pos,selectedItems);
        holder.setOnViewHolderItemClickListener(() -> {
            if(selectedItems.get(pos)) {
                selectedItems.delete(pos);
            }else {
                selectedItems.delete(prePosition);
                selectedItems.put(pos, true);
            }
            if (prePosition != -1) notifyItemChanged(prePosition);
            notifyItemChanged(pos);
            prePosition = pos;
        });
        holder.callBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactArrayList.get(pos).getPhoneNumber()));
            view.getContext().startActivity(intent);
        });
        holder.smsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+contactArrayList.get(pos).getPhoneNumber()));
            view.getContext().startActivity(intent);
        });
        holder.optBtn.setOnClickListener(view -> showMenu(view, R.menu.item_menu, pos));
    }

    @Override
    public int getItemCount() {
        return contactArrayList == null ? 0 : contactArrayList.size();
    }

    public void setList(List<Contact> data) {
        contactArrayList = data;
        notifyDataSetChanged();
    }

    private void showMenu(View v, @MenuRes int menuRes, int pos) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_menu_delete:
                        new Thread(() -> ContactDB.getInstance(v.getContext()).contactDao().deleteContact(contactArrayList.get(pos))).start();
                        selectedItems.delete(pos);
                        prePosition = -1;
                        notifyItemRemoved(pos);
                        return false;
                    case R.id.item_menu_edit:
                        Bundle args = new Bundle();
                        args.putInt("id", contactArrayList.get(pos).getId());
                        ContactEditBottomSheet contactEditBottomSheet = new ContactEditBottomSheet();
                        contactEditBottomSheet.setArguments(args);
                        contactEditBottomSheet.show(fragmentManager, null);
                        notifyDataSetChanged();
                        return false;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTv;
        public TextView phoneNumTv;
        public TextView emailTv;
        public TextView memoTv;
        public TextView dateTv;
        public Button callBtn;
        public Button smsBtn;
        public ImageButton optBtn;
        public ConstraintLayout contactDefaultLayout;
        public ConstraintLayout contactExpandedLayout;

        OnViewHolderItemClickListener onViewHolderItemClickListener;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.contact_name);
            phoneNumTv = itemView.findViewById(R.id.contact_phone);
            emailTv = itemView.findViewById(R.id.contact_email);
            memoTv = itemView.findViewById(R.id.contact_memo);
            dateTv = itemView.findViewById(R.id.contact_date);
            contactDefaultLayout = itemView.findViewById(R.id.contact_default);
            contactExpandedLayout = itemView.findViewById(R.id.contact_expanded);
            callBtn = itemView.findViewById(R.id.contact_call);
            smsBtn = itemView.findViewById(R.id.contact_sms);
            optBtn = itemView.findViewById(R.id.contact_more_opt);

            contactDefaultLayout.setOnClickListener(view -> onViewHolderItemClickListener.onViewHolderItemClick());
        }
        public void onBind(Contact contact, int position, SparseBooleanArray selectedItems) {
            nameTv.setText(contact.getName());
            phoneNumTv.setText(contact.getPhoneNumber());
            emailTv.setText(contact.getEmail());
            memoTv.setText(contact.getMemo());
            dateTv.setText(String.format("최종 수정일 : %s", contact.getTimeStamp()));
            changeVisibility(selectedItems.get(position));
        }

        private void changeVisibility(final boolean isExpanded) {
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0,380) : ValueAnimator.ofInt(380,0);
            va.setDuration(500);
            va.addUpdateListener(valueAnimator -> {
                contactExpandedLayout.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
                contactExpandedLayout.requestLayout();

                contactExpandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            });
            va.start();
        }

        public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
            this.onViewHolderItemClickListener = onViewHolderItemClickListener;
        }
    }
}

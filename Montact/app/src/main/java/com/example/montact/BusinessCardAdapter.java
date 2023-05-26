package com.example.montact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.BusinessCardViewHolder> {

    private List<BizCard> bizCardList;
    private String imgPath;
    private Context context;

    BusinessCardAdapter(List<BizCard> dataList) {
        bizCardList = dataList;
    }

    @NonNull
    @Override
    public BusinessCardAdapter.BusinessCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bizcard,parent,false);
        imgPath = parent.getContext().getFilesDir().toString();
        context = parent.getContext();
        return new BusinessCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessCardAdapter.BusinessCardViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        holder.onBind(bizCardList.get(pos), pos);
    }

    @Override
    public int getItemCount() { return bizCardList == null ? 0 : bizCardList.size(); }

    public void setList(List<BizCard> data) {
        bizCardList = data;
        notifyDataSetChanged();
        Log.d("TESTTEST",getItemCount()+"");
    }

    public class BusinessCardViewHolder extends RecyclerView.ViewHolder {

        public ImageView bizCardImg;

        public BusinessCardViewHolder(@NonNull View itemView) {
            super(itemView);
            bizCardImg = itemView.findViewById(R.id.item_bizcard_img);
        }

        public void onBind(BizCard bizCard, int position) {
            String bizCardPath = imgPath + "/picture/" + bizCard.getImgName() + ".jpg";
            Bitmap bitmap = BitmapFactory.decodeFile(bizCardPath);
            bizCardImg.setImageBitmap(bitmap);
            bizCardImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BusinessCardEditActivity.class);
                    intent.putExtra("bizCardId", bizCard.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}

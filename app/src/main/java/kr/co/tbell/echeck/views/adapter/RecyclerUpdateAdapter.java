package kr.co.tbell.echeck.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.HouseItem;

public class RecyclerUpdateAdapter extends RecyclerView.Adapter<RecyclerUpdateAdapter.MyUpdateViewAdapter> {

    Context mContext;
    List<HouseItem> list;
    private RecyclerUpdateAdapter.UpdateItemClickListener updateItemClickListener;

    public RecyclerUpdateAdapter(Context mContext, List<HouseItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public interface UpdateItemClickListener {
        void onUpdateItemClick(View v, int position);
        void onDeleteItemClick(View v, int position);
    }

    public void setHouseItemClickListener(RecyclerUpdateAdapter.UpdateItemClickListener updateItemClickListener) {
        this.updateItemClickListener = updateItemClickListener;
    }


    @NonNull
    @Override
    public MyUpdateViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_update_view, parent, false);
        return new RecyclerUpdateAdapter.MyUpdateViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUpdateViewAdapter holder, int position) {
        holder.titleText.setText(list.get(position).getTitleText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyUpdateViewAdapter  extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView deleteBtn;
        ImageView updateBtn;

        public MyUpdateViewAdapter(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            deleteBtn = itemView.findViewById(R.id.delete_icon);
            updateBtn = itemView.findViewById(R.id.update_icon);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        if(updateItemClickListener != null) {
                            updateItemClickListener.onDeleteItemClick(v, position);
                        }
                    }
                }
            });

            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        if(updateItemClickListener != null) {
                            updateItemClickListener.onUpdateItemClick(v, position);
                        }
                    }
                }
            });
        }
    }
}


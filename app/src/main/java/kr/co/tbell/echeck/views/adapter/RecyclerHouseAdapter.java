package kr.co.tbell.echeck.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import kr.co.tbell.echeck.R;

import kr.co.tbell.echeck.model.dto.HouseItem;

public class RecyclerHouseAdapter extends RecyclerView.Adapter<RecyclerHouseAdapter.MyHouseViewAdapter> {

    Context mContext;
    List<HouseItem> list;
    private HouseItemClickListener houseItemClickListener;

    public RecyclerHouseAdapter(Context mContext, List<HouseItem> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public interface HouseItemClickListener {
        void onHouseItemClick(View v, int position);
    }

    public void setHouseItemClickListener(HouseItemClickListener houseItemClickListener) {
        this.houseItemClickListener = houseItemClickListener;
    }

    @NonNull
    @Override
    public MyHouseViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.house_list_view, parent, false);
        return new RecyclerHouseAdapter.MyHouseViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHouseViewAdapter holder, int position) {
        holder.titleText.setText(list.get(position).getTitleText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHouseViewAdapter extends RecyclerView.ViewHolder {
        TextView titleText;

        public MyHouseViewAdapter(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        if(houseItemClickListener != null) {
                            houseItemClickListener.onHouseItemClick(v, position);
                        }
                    }
                }
            });
        }
    }
}

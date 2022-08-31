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

import kr.co.tbell.echeck.model.ElectList;

public class RecyclerResultAdapter extends RecyclerView.Adapter<RecyclerResultAdapter.MyListAdapter> {

    private Context mContext;
    private List<ElectList> list;
    private ElectItemClickListener electItemClickListener;

    public RecyclerResultAdapter(Context mContext, List<ElectList> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public interface ElectItemClickListener {
        void onElectItemClick(View v, int position);
    }

    public void setElectItemClickListener(RecyclerResultAdapter.ElectItemClickListener electItemClickListener) {
        this.electItemClickListener = electItemClickListener;
    }

    @NonNull
    @Override
    public MyListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usage_list_view, parent, false);

        return new MyListAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListAdapter holder, int position) {

        holder.thumbImage.setImageResource(list.get(position).getThumbImg());
        holder.titleText.setText(list.get(position).getTitleText());
        holder.detailText.setText(list.get(position).getDetailText());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyListAdapter extends RecyclerView.ViewHolder {

        ImageView thumbImage;
        TextView titleText;
        TextView detailText;

        public MyListAdapter(@NonNull View itemView) {
            super(itemView);

            thumbImage = itemView.findViewById(R.id.list_thumbnail);
            titleText = itemView.findViewById(R.id.list_date);
            detailText = itemView.findViewById(R.id.list_usage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        if(electItemClickListener != null) {
                            electItemClickListener.onElectItemClick(v, position);
                        }
                    }
                }
            });
        }
    }
}
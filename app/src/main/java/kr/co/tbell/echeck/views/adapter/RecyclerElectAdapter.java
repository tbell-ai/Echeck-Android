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

import kr.co.tbell.echeck.model.dto.ElectDialogList;
import kr.co.tbell.echeck.R;


public class RecyclerElectAdapter extends RecyclerView.Adapter<RecyclerElectAdapter.MyListAdapter> {

    private Context mContext;
    private List<ElectDialogList> list;
    private ItemClickListener itemClickListener;

    public RecyclerElectAdapter(Context mContext, List<ElectDialogList> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setElectItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.measure_list_view, parent, false);

        return new MyListAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListAdapter holder, int position) {

        holder.thumbImage.setImageResource(list.get(position).getThumbImg());
        holder.title.setText(list.get(position).getTitle().substring(0, 10));
        holder.usage.setText(list.get(position).getUsage());
        holder.charge.setText(list.get(position).getCharge());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyListAdapter extends RecyclerView.ViewHolder {

        ImageView thumbImage;
        TextView title;
        TextView usage;
        TextView charge;

        public MyListAdapter(@NonNull View itemView) {
            super(itemView);

            thumbImage = itemView.findViewById(R.id.list_thumbnail);
            title = itemView.findViewById(R.id.list_date);
            usage = itemView.findViewById(R.id.list_usage);
            charge = itemView.findViewById(R.id.list_charge);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        if(itemClickListener != null) {
                            itemClickListener.onItemClick(v, position);
                        }
                    }
                }
            });
        }
    }
}
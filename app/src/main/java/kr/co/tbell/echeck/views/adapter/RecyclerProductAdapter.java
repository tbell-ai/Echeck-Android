package kr.co.tbell.echeck.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.tbell.echeck.R;
import java.util.List;

public class RecyclerProductAdapter extends RecyclerView.Adapter<RecyclerProductAdapter.MyListAdapter> {

    private Context mContext;
    private List<String> list;
    private ItemClickListener itemClickListener;

    public RecyclerProductAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setProductItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_product_list_view, parent, false);

        return new MyListAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListAdapter holder, int position) {
        holder.title.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyListAdapter extends RecyclerView.ViewHolder {

        TextView title;

        public MyListAdapter(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.list_product_name);

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

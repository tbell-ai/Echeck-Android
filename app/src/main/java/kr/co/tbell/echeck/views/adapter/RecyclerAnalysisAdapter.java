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
import kr.co.tbell.echeck.model.dto.HomeProduct;

public class RecyclerAnalysisAdapter extends RecyclerView.Adapter<RecyclerAnalysisAdapter.MyListAdapter> {

    private Context mContext;
    private List<HomeProduct> list;

    public RecyclerAnalysisAdapter(Context mContext, List<HomeProduct> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public MyListAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.analysis_list_view, parent, false);

        return new RecyclerAnalysisAdapter.MyListAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListAdapter holder, int position) {

        holder.thumbImage.setImageResource(R.drawable.main_icon3);
        holder.productName.setText(list.get(position).getProduct());
        holder.productPattern.setText(list.get(position).getUsagePattern());
        holder.productDayHour.setText(list.get(position).getDayHour());
        holder.productPercentage.setText(list.get(position).getPersentage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyListAdapter extends RecyclerView.ViewHolder {

        ImageView thumbImage;
        TextView productName;
        TextView productPattern;
        TextView productDayHour;
        TextView productPercentage;

        public MyListAdapter(@NonNull View itemView) {
            super(itemView);

            thumbImage = itemView.findViewById(R.id.list_thumbnail);
            productName = itemView.findViewById(R.id.product_name);
            productPattern = itemView.findViewById(R.id.product_pattern);
            productDayHour = itemView.findViewById(R.id.product_day_hour);
            productPercentage = itemView.findViewById(R.id.product_usage_persent);

        }
    }
}

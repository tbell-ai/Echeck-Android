package kr.co.tbell.echeck.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.model.dto.FaqItem;

public class RecyclerFaqAdapter extends RecyclerView.Adapter<RecyclerFaqAdapter.FaqViewHolder> {

    List<FaqItem> faqList;

    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;

    public RecyclerFaqAdapter(List<FaqItem> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_card, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {

        FaqItem faq = faqList.get(position);
        holder.answerTxt.setText(faq.getAnswer());
        holder.questionTxt.setText(faq.getQuestion());

        boolean isExpandable = faqList.get(position).isExpandable();

        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public class FaqViewHolder extends RecyclerView.ViewHolder {

        TextView answerTxt;
        TextView questionTxt;

        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);

            answerTxt = itemView.findViewById(R.id.answer);
            questionTxt = itemView.findViewById(R.id.question);

            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if(prePosition != -1) {

                        if(prePosition == position) {
                            FaqItem faq = faqList.get(prePosition);
                            faq.setExpandable(!faq.isExpandable());
                            notifyItemChanged(prePosition);
                        } else {

                            FaqItem preFaq = faqList.get(prePosition);
                            preFaq.setExpandable(false);
                            notifyItemChanged(prePosition);

                            FaqItem faq = faqList.get(position);
                            faq.setExpandable(true);
                            notifyItemChanged(position);
                            prePosition = position;

                        }

                    } else {
                        FaqItem faq = faqList.get(position);
                        faq.setExpandable(!faq.isExpandable());
                        notifyItemChanged(position);
                        prePosition = position;
                    }

                }
            });
        }
    }
}

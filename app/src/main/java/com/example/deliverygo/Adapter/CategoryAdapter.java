package com.example.deliverygo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deliverygo.Domain.CategoryModel;
import com.example.deliverygo.R;
import com.example.deliverygo.databinding.ViewholderCategoryBinding;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final ArrayList<CategoryModel> items;
    private Context context;
    private int selectedPosition = 0;

    public interface OnItemClickListener {
        void onClick(CategoryModel category);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(ArrayList<CategoryModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel item = items.get(position);
        holder.binding.titlTxtCat.setText(item.getTitle());

        holder.binding.getRoot().setOnClickListener(v -> {
            int lastSelectedPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onClick(item);
            }
        });

        if (selectedPosition == position) {
            holder.binding.titlTxtCat.setBackgroundResource(R.drawable.cat_bg);
            holder.binding.titlTxtCat.setTextColor(context.getResources().getColor(com.example.deliverygo.R.color.white));
        } else {
            holder.binding.titlTxtCat.setBackgroundResource(R.drawable.stroke_bg);
            holder.binding.titlTxtCat.setTextColor(context.getResources().getColor(com.example.deliverygo.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCategoryBinding binding;

        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

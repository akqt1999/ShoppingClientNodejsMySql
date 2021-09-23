package etn.app.danghoc.shoppingclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.ConstantCallSite;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.DanhMucItemClick;
import etn.app.danghoc.shoppingclient.Interface.IOnRecycleViewClickListener;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.R;

public class CategorySanPhamAdapter extends RecyclerView.Adapter<CategorySanPhamAdapter.ViewHolder> {

    Context context;
    List<CategoryProduct>categoryProducts;

    public CategorySanPhamAdapter(Context context, List<CategoryProduct> categoryProducts) {
        this.context = context;
        this.categoryProducts = categoryProducts;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_category_product,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.txt_name.setText(categoryProducts.get(position).getTenDM());
            holder.setListener((view, position1) -> {
                Common.selectCategprySelect=categoryProducts.get(position1);
                        EventBus.getDefault().postSticky(new DanhMucItemClick(true,categoryProducts.get(position1).getIdDanhMuc()));
            });
    }

    @Override
    public int getItemCount() {
        return categoryProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_name)
        TextView txt_name;
        Unbinder unbinder;

        IOnRecycleViewClickListener listener;

        public void setListener(IOnRecycleViewClickListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull @NotNull View itemView) {

            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }
}

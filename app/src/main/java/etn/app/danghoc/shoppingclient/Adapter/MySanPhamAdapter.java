package etn.app.danghoc.shoppingclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Callback.IClickItemSanPham;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.SanPhamItemClick;
import etn.app.danghoc.shoppingclient.Interface.IOnRecycleViewClickListener;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Model.Tinh;
import etn.app.danghoc.shoppingclient.R;

public class MySanPhamAdapter extends RecyclerView.Adapter<MySanPhamAdapter.MyViewHolder> {

    private List<SanPham> sanPhamList;
    Context context;
    private IClickItemSanPham itemSanPham;

    public MySanPhamAdapter( Context context,List<SanPham> sanPhamList,IClickItemSanPham itemSanPham) {
        this.sanPhamList = sanPhamList;
        this.context = context;
        this.itemSanPham=itemSanPham;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_sanpham_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        Picasso.get().load(sanPhamList.get(position).getHinh()).into(holder.img_sanpham);
        holder.txt_sanpham_name.setText( sanPhamList.get(position).getTenSP());
        holder.txt_sanpham_gia.setText("giá: "+Common.formatPrice(sanPhamList.get(position).getGiaSP()));

        for (Tinh item: Common.provinceList) {
            if(sanPhamList.get(position).getProvinceId()==item.getProvinceID())
            {
                holder.txt_khuvuc_ban.setText(new StringBuilder("khu vực bán: ").append(item.getProvinceName()));
                break;
            }
        }

        holder.setListener((view, position1) -> {
            Common.selectSanPham = sanPhamList.get(position1);
            EventBus.getDefault().postSticky(new SanPhamItemClick(true, sanPhamList.get(position1)));
            itemSanPham.onClickItemUser();
        });

    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.txt_sanpham_name)
        TextView txt_sanpham_name;
        @BindView(R.id.txt_sanpham_gia)
        TextView txt_sanpham_gia;
        @BindView(R.id.txt_khuvuc_ban)
        TextView txt_khuvuc_ban;

        @BindView(R.id.img_sanpham)
        ImageView img_sanpham;

        Unbinder unbinder;

        IOnRecycleViewClickListener listener;

        public void setListener(IOnRecycleViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull @NotNull View itemView) {
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

package etn.app.danghoc.shoppingclient.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.CartIsChoose;
import etn.app.danghoc.shoppingclient.EventBus.CartItemDelete;
import etn.app.danghoc.shoppingclient.EventBus.MyProductItemDelete;
import etn.app.danghoc.shoppingclient.EventBus.MyProductItemEdit;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.R;

    public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {
    Context context;
    List<SanPham> sanPhams;


    public MyProductAdapter(Context context, List<SanPham> cartItems) {
        this.context = context;
        this.sanPhams = cartItems;

    }

    @NonNull
    @Override
    public MyProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyProductAdapter.ViewHolder(LayoutInflater.
                from(context).inflate(R.layout.item_my_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(sanPhams.get(position).getHinh())
                .into(holder.imgCart);
        holder.txtFoodName.setText(sanPhams.get(position).getTenSP());
        holder.txtFoodPrice.setText(Common.formatPrice(sanPhams.get(position).getGiaSP()));
        holder.txt_status.setText(Common.convertStatusMyProduct(sanPhams.get(position).getTrangthai()));


        //event
        holder.btn_delete.setOnClickListener(view -> {
         //   EventBus.getDefault().postSticky(new CartItemDelete(true, position));

            if(sanPhams.get(position).getTrangthai()==0)
            {
                Toast.makeText(context, "đang chờ bạn xác nhận cho người bán không thể xóa", Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setMessage("bạn có thực sự muốn xoá");
            builder.setPositiveButton("xoa", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    EventBus.getDefault().postSticky(new MyProductItemDelete(true,position));
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            Dialog dialog=builder.create();
            dialog.show();
        });

        holder.btn_edit.setOnClickListener(view -> {
            if(sanPhams.get(position).getTrangthai()==0){
                Toast.makeText(context, "đang chờ bạn xác nhận cho người bán không thể sua", Toast.LENGTH_LONG).show();
                return;
            }
            if(sanPhams.get(position).getTrangthai()==1){
                Toast.makeText(context, "bạn đã bán rồi,không sửa được", Toast.LENGTH_LONG).show();
                return;
            }


            EventBus.getDefault().postSticky(new MyProductItemEdit(true,position) );

        });

    }

    @Override
    public int getItemCount() {
        return sanPhams.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        @BindView(R.id.imgCart)
        ImageView imgCart;
        @BindView(R.id.btn_delete)
        ImageButton btn_delete;
        @BindView(R.id.btn_edit)
        ImageButton btn_edit;
        @BindView(R.id.txt_price)
        TextView txtFoodPrice;
        @BindView(R.id.txtFoodName)
        TextView txtFoodName;
        @BindView(R.id.txt_status)
        TextView txt_status;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}

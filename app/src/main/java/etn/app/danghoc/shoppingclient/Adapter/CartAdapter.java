package etn.app.danghoc.shoppingclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.CartIsChoose;
import etn.app.danghoc.shoppingclient.EventBus.CartItemDelete;
import etn.app.danghoc.shoppingclient.EventBus.SanPhamItemClick;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    List<Cart> cartItems;
    Gson gson;

    //cai "cartItems" nay la khong su dung
    public CartAdapter(Context context, List<Cart> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        gson = new Gson();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.
                from(context).inflate(R.layout.layout_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(cartItems.get(position).getHinh())
                .into(holder.imgCart);
        holder.txtFoodName.setText(cartItems.get(position).getTenSP());
        holder.txtFoodPrice.setText(Common.formatPrice(cartItems.get(position).getGia()));

//        if (cartItems.get(position).isChoose())
//            holder.checkbox.setChecked(true);
//        else
//            holder.checkbox.setChecked(false);

            //event
            holder.btn_delete.setOnClickListener(view -> {
                EventBus.getDefault().postSticky(new CartItemDelete(true, position));
            });

//        holder.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b)
//                Common.cartList.get(position).setChoose(true);//cot loi nam o day
//            else
//                Common.cartList.get(position).setChoose(false);
//
//            EventBus.getDefault().postSticky(new CartIsChoose(true));
//
//        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        @BindView(R.id.imgCart)
        ImageView imgCart;
        @BindView(R.id.btn_delete)
        ImageButton btn_delete;
        @BindView(R.id.txt_price)
        TextView txtFoodPrice;
        @BindView(R.id.txtFoodName)
        TextView txtFoodName;
//        @BindView(R.id.checkbox)
//        CheckBox checkbox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}

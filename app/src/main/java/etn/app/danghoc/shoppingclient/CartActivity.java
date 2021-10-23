package etn.app.danghoc.shoppingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.CartAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.CartIsChoose;
import etn.app.danghoc.shoppingclient.EventBus.CartItemDelete;
import etn.app.danghoc.shoppingclient.EventBus.HideFABCart;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.ui.cart.ConfirmOrderDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    List<Cart> cartList = new ArrayList<>();


    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.txtTotalPrice)
    TextView txtTotalPrice;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    CartAdapter adapter;

     IMyShoppingAPI myRestaurantAPI;
     CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(this);
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);

        initToolbar();
        displayCart();

    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



    }

    @OnClick(R.id.btn_place_order)
    void placeOrderClick() {


        boolean isChoose = true;
        for (int i = 0; i < Common.cartList.size(); i++) {
            if (Common.cartList.get(i).isChoose()) {
                isChoose = false;
                break;
            }
        }

        if (isChoose)
            Toast.makeText(this, "chưa chọn đơn hàng cần mua", Toast.LENGTH_SHORT).show();
        else {
            DialogFragment dialog = ConfirmOrderDialog.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            dialog.show(fm, "tag");
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void deleteItemCart(CartItemDelete event) {
        if (event.isSuccess()) {

            int position = event.getPosition();

         progress_bar.setVisibility(View.VISIBLE);
            compositeDisposable.
                    add(myRestaurantAPI.deleteCart(
                            Common.API_KEY,
                            Common.currentUser.getIdUser(),
                            Common.cartList.get(position).getIdSP()//khi xoa thi cai common no cung xoa theo
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(cartModel -> {
                                        if (cartModel.isSuccess()) {

                                            cartList.remove(position);
                                            adapter.notifyDataSetChanged();
                                        progress_bar.setVisibility(View.GONE);
                                            totalPrice();
                                            Toast.makeText(this, "size common" + Common.cartList.size(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(this, "delete success", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "[DELETE CART]" + cartModel.getMessage(), Toast.LENGTH_SHORT).show();
                                            progress_bar.setVisibility(View.GONE);
                                        }

                                    }, throwable -> {
                                        progress_bar.setVisibility(View.GONE);
                                        Toast.makeText(this, "[DELETE CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
        }
    }

    //
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void CartIsChoose(CartIsChoose event) {
        if (event.isSuccess()) {
            totalPrice();
        }
    }


    public void totalPrice() {
        float totalPrice = 0;

        for (int i = 0; i < Common.cartList.size(); i++) {
            if (Common.cartList.get(i).isChoose()) {
                totalPrice = totalPrice + Common.cartList.get(i).getGia();
            }
        }

        double totalPriceFinal = Double.parseDouble(totalPrice + ""), displayPrice = 0.0;
        displayPrice = totalPriceFinal;
        displayPrice = Math.round(displayPrice * 100.0 / 100.0);
        txtTotalPrice.setText(new StringBuilder().append(Common.formatPrice(displayPrice)));
        Common.totalPriceFromCart = totalPriceFinal;
    }

    private void displayCart() {
        compositeDisposable.
                add(myRestaurantAPI.getCart(Common.API_KEY,Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cartModel -> {

                            progress_bar.setVisibility(View.GONE);

                            if(cartModel.isSuccess())
                            {
                                Common.cartList = cartModel.getResult();
                                cartList=cartModel.getResult();
                                Collections.reverse(cartList);

                                if (cartList.size() == 0) {
                                    recyclerCart.setVisibility(View.GONE);
                                    group_place_holder.setVisibility(View.GONE);
                                    txt_empty_cart.setVisibility(View.VISIBLE);
                                } else {
                                    recyclerCart.setVisibility(View.VISIBLE);
                                    group_place_holder.setVisibility(View.VISIBLE);
                                    txt_empty_cart.setVisibility(View.GONE);

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                                    recyclerCart.setLayoutManager(linearLayoutManager);
                                    adapter = new CartAdapter(this, cartList);
                                    recyclerCart.setAdapter(adapter);
                                }
                            }
                            else{
                                progress_bar.setVisibility(View.GONE);
                                txt_empty_cart.setVisibility(View.VISIBLE);
                                Toast.makeText(this,"[cart]"+ cartModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        },throwable -> {
                            progress_bar.setVisibility(View.GONE);
                            Toast.makeText(this,"[cart]"+ throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onStart() {


        super.onStart();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        EventBus.getDefault().postSticky(new HideFABCart(true));
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().postSticky(new CartItemDelete(false, -1));// khi thoat la no se xoa cai event bus
        EventBus.getDefault().postSticky(new CartIsChoose(false));

        super.onPause();
    }


    @Override
    public void onStop() {


        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);


        super.onStop();
    }
}
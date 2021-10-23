package etn.app.danghoc.shoppingclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderBySellerClick;
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderSellerAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.UpdateStatusOrder;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.sendNotificationPack.APIService;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Client;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Data;
import etn.app.danghoc.shoppingclient.sendNotificationPack.MyResponse;
import etn.app.danghoc.shoppingclient.sendNotificationPack.NotificationSender;
import etn.app.danghoc.shoppingclient.ui.view_order_seller.OrderDetailDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderSeller extends AppCompatActivity {


    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;
  ;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    List<Order> orderList;
    ViewOrderSellerAdapter adapter;

     APIService apiService;
     IMyShoppingAPI myRestaurantAPI;
     CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_seller);
        ButterKnife.bind(this);

        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        apiService= Client.getInstance().create(APIService.class);

        initToolbar();
        displayOrder();
    }

    private void displayOrder() {

        compositeDisposable.
                add(myRestaurantAPI.getOrdersBySeller(Common.API_KEY,
                        Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderModel -> {
                            progress_bar.setVisibility(View.GONE);
                            if(orderModel.isSuccess())
                            {
                                orderList=orderModel.getResult() ;
                                Common.orderSellerList=orderModel.getResult();

                                adapter = new ViewOrderSellerAdapter(this, orderList);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                                recycler_orders.setLayoutManager(linearLayoutManager);
                                recycler_orders.setAdapter(adapter);
                            }

                        },throwable -> {
                            progress_bar.setVisibility(View.GONE);
                        })
                );


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



    }

    @Override
    public void onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        EventBus.getDefault().postSticky(new UpdateStatusOrder(-99, -1));
        super.onStart();
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(toString()))
            EventBus.getDefault().unregister(this);
        EventBus.getDefault().postSticky(new UpdateStatusOrder(-99, -1));
        EventBus.getDefault().postSticky(new ViewOrderBySellerClick(false));
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onClickOrder(ViewOrderBySellerClick event) {
        if(event.isSuccess())
        {
            Toast.makeText(this, "nhan 123", Toast.LENGTH_SHORT).show();
            FragmentManager fm = getSupportFragmentManager();
            OrderDetailDialog dialog = OrderDetailDialog.newInstance();
            dialog.show(fm, "tag12212");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void cancelOrder(UpdateStatusOrder event) {//-2 huy don hang boi nguoi ban
        if (event.getStatus()==-2) {
            int position = event.getPosition();

            compositeDisposable.
                    add(myRestaurantAPI.updateStatusOrder(
                            Common.API_KEY,
                            Common.orderSellerList.get(position).getIdDonHang(),
                            -2
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(updateStatusModel -> {
                                        if (updateStatusModel.isSuccess()) {

                                            String message="nguoi ban da huy don hang cua ban";
                                            sendNotificationChangeStatus("huy bo",message,event.getIdUser());


                                            Common.orderSellerList.get(position).setTrangThai(-2);
                                            orderList.get(position).setTrangThai(-2);

                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(ViewOrderSeller.this, "cancel order success", Toast.LENGTH_SHORT).show();

                                        } else
                                            Toast.makeText(ViewOrderSeller.this, "update status fail", Toast.LENGTH_SHORT).show();

                                    }, throwable -> {
                                        Toast.makeText(ViewOrderSeller.this, "[update status]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("zxc",throwable.getMessage());
                                    })
                    );
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void confirmOrder(UpdateStatusOrder event) {//1 nguoi ban xac don hang
        if (event.getStatus()==1) {
            int position = event.getPosition();

            compositeDisposable.
                    add(myRestaurantAPI.updateStatusOrder(
                            Common.API_KEY,
                            Common.orderSellerList.get(position).getIdDonHang(),
                            1
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(updateStatusModel -> {
                                        if (updateStatusModel.isSuccess()) {

                                            String message="nguoi ban da xac nhan don hang cua ban";
                                            sendNotificationChangeStatus("xac nhan",message,event.getIdUser());

                                            Common.orderSellerList.get(position).setTrangThai(1);
                                            orderList.get(position).setTrangThai(1);

                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(ViewOrderSeller.this, "cancel order success", Toast.LENGTH_SHORT).show();

                                        } else
                                            Toast.makeText(ViewOrderSeller.this, "update status fail", Toast.LENGTH_SHORT).show();

                                    }, throwable -> {
                                        Toast.makeText(ViewOrderSeller.this, "[update status]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
        }
    }

    private void sendNotificationChangeStatus(String title, String message, String idSeller) {


        Data data = new Data(title, message);


        FirebaseDatabase.getInstance()
                .getReference().child("Tokens").child(idSeller).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String usertoken=snapshot.getValue(String.class);
                        NotificationSender sender = new NotificationSender(data, usertoken);

                        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code()==200)
                                {
                                    if(response.body().success!=1){
                                        Toast.makeText(ViewOrderSeller.this, "send Notification Fail", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ViewOrderSeller.this, "send notifi success", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(ViewOrderSeller.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(ViewOrderSeller.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
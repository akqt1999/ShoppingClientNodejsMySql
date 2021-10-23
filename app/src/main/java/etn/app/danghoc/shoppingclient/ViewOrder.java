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
import etn.app.danghoc.shoppingclient.Adapter.ViewOrderAdapter;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.EventBus.UpdateStatusOrder;
import etn.app.danghoc.shoppingclient.EventBus.ViewOrderByBuyerClick;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import etn.app.danghoc.shoppingclient.sendNotificationPack.APIService;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Client;
import etn.app.danghoc.shoppingclient.sendNotificationPack.Data;
import etn.app.danghoc.shoppingclient.sendNotificationPack.MyResponse;
import etn.app.danghoc.shoppingclient.sendNotificationPack.NotificationSender;
import etn.app.danghoc.shoppingclient.ui.view_order.OrderDetailDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrder extends AppCompatActivity {

    Unbinder unbinder;
    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;
    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;
    ViewOrderAdapter adapter;

    List<Order> orderList;

     IMyShoppingAPI myRestaurantAPI;
     APIService apiService;
     CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ViewOrder() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        ButterKnife.bind(this);

        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
        apiService= Client.getInstance().create(APIService.class);

        initToolbar();
        displayOrders();

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



    }

    private void displayOrders() {

        compositeDisposable.
                    add(myRestaurantAPI.getOrdersByBuyer(Common.API_KEY,
                        Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderModel -> {
                            if(orderModel.isSuccess())
                            {

                                orderList=orderModel.getResult();
                                Common.orderList=orderModel.getResult();

                                adapter = new ViewOrderAdapter(this, orderList);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                                recycler_orders.setLayoutManager(linearLayoutManager);
                                recycler_orders.setAdapter(adapter);

                                Log.d("loix",orderModel.getResult().size()+"");
                            }
                            else {
                                Toast.makeText(this, "[view order]"+orderModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progress_bar.setVisibility(View.GONE);
                        },throwable -> {
                            Log.d("loix",throwable.getMessage());
                            progress_bar.setVisibility(View.GONE);
                        })
                );


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
        EventBus.getDefault().postSticky(new ViewOrderByBuyerClick(false));

        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public  void clickOrder(ViewOrderByBuyerClick event){
        if (event.isSuccess()){
            FragmentManager fm = getSupportFragmentManager();
            OrderDetailDialog dialog=OrderDetailDialog.newInstance();
            dialog.show(fm ,"12354");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void cancelOrder(UpdateStatusOrder event) { //-2: huy don hang boi  ban
        if (event.getStatus()==2) {

            int position = event.getPosition();

            compositeDisposable.
                    add(myRestaurantAPI.updateStatusOrder(
                            Common.API_KEY,
                            Common.orderList.get(position).getIdDonHang(),
                            2
                            )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(updateStatusModel -> {
                                        if (updateStatusModel.isSuccess()) {
                                            String message=new StringBuilder()
                                                    .append("nguoi mua da huy don hang cua ban").toString();
                                            sendNotificationChangeStatus("huy hang"
                                                    ,message,event.getIdUser());
                                            Common.orderList.get(position).setTrangThai(2);
                                            orderList.get(position).setTrangThai(2);

                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(ViewOrder.this, "cancel order success", Toast.LENGTH_SHORT).show();

                                        } else
                                            Toast.makeText(ViewOrder.this, "update status fail", Toast.LENGTH_SHORT).show();

                                    }, throwable -> {
                                        Toast.makeText(ViewOrder.this, "[update status]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    })
                    );
        }
    }

    private void sendNotificationChangeStatus(String title, String message, String idBuyer) {


        Data data = new Data(title, message);


        FirebaseDatabase.getInstance()
                .getReference().child("Tokens").child(idBuyer).child("token")
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
                                        Toast.makeText(ViewOrder.this, "send Notification Fail", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ViewOrder.this, "send notifi success", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(ViewOrder.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(ViewOrder.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
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
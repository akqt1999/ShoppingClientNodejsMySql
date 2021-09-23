package etn.app.danghoc.shoppingclient.ui.view_order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.Model.Order;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrderViewModel extends ViewModel {

    private MutableLiveData<String> messageError;
    private MutableLiveData<List<Order>> orderList;

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ViewOrderViewModel() {
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
    }

    public MutableLiveData<List<Order>> getOrderList() {
        if(orderList==null)
        {
            orderList=new MutableLiveData<>();
            messageError=new MutableLiveData<>();
            loadOrderList();
        }
        return orderList;
    }

    private void loadOrderList() {
        compositeDisposable.
                add(myRestaurantAPI.getOrdersByBuyer(Common.API_KEY,
                        Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderModel -> {
                            if(orderModel.isSuccess())
                            {
                                orderList.setValue(orderModel.getResult());
                            }

                        },throwable -> {
                            messageError.setValue(throwable.getMessage());
                        })
                );
    }

    public LiveData<String> getMessageError() {
        if(messageError==null)
            messageError=new MutableLiveData<>();
        return messageError;
    }
}
package etn.app.danghoc.shoppingclient.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private MutableLiveData<String> messageError;
    private MutableLiveData<List<Cart>> listCart;


    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CartViewModel() {
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);
    }

    public MutableLiveData<List<Cart>> getListCart() {
        if(listCart==null)
        {
            listCart=new MutableLiveData<>();
            messageError=new MutableLiveData<>();
            loadListCart();
        }
        return listCart;
    }

    public MutableLiveData <String>getMessageError(){
        if(messageError==null)
            messageError=new MutableLiveData<>();
        return messageError;
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    private void loadListCart() {
        compositeDisposable.
                add(myRestaurantAPI.getCart(Common.API_KEY,Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(cartModel -> {
                            if(cartModel.isSuccess())
                            {
                                listCart.setValue(cartModel.getResult());
                            }
                            else{
                                messageError.setValue(cartModel.getMessage());
                            }

                        },throwable -> {
                            messageError.setValue(throwable.getMessage());
                        })
                );
    }
}
package etn.app.danghoc.shoppingclient.ui.category;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.Cart;
import etn.app.danghoc.shoppingclient.Model.CategoryProduct;
import etn.app.danghoc.shoppingclient.Model.DanhMucModel;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CategoryViewModel extends ViewModel {

    private MutableLiveData<String> messageError;
    private MutableLiveData<List<CategoryProduct>> listCategory;

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public CategoryViewModel() {
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);

    }

    public MutableLiveData<List<CategoryProduct>> getListCategory() {
        if (listCategory == null) {
            listCategory = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadListCategory();
        }
        return listCategory;
    }

    private void loadListCategory() {
        compositeDisposable.
                add(myRestaurantAPI.getDanhMuc(Common.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(model -> {
                            if (model.isSuccess()) {
                                listCategory.setValue(model.getResult());
                            } else {
                                messageError.setValue(model.getMessage());
                            }

                        }, throwable -> {
                            messageError.setValue(throwable.getMessage());
                        })
                );
    }


    public MutableLiveData<String> getMessageError() {
        return messageError;
    }
}
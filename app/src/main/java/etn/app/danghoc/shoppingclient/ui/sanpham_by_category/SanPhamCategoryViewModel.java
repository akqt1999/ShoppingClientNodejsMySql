package etn.app.danghoc.shoppingclient.ui.sanpham_by_category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import etn.app.danghoc.shoppingclient.Callback.ISanPhamCallbackListener;
import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.SanPham;
import etn.app.danghoc.shoppingclient.Retrofit.IMyShoppingAPI;
import etn.app.danghoc.shoppingclient.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SanPhamCategoryViewModel extends ViewModel {
    private MutableLiveData<String> messageError;
    private MutableLiveData<List<SanPham>> listSanPham;

    private IMyShoppingAPI myRestaurantAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ISanPhamCallbackListener restaurantCallbackListener;



    public SanPhamCategoryViewModel() {
        myRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyShoppingAPI.class);

    }

    public MutableLiveData<List<SanPham>> getListSanPham() {
        if(listSanPham==null)
        {
            listSanPham=new MutableLiveData<>();
            messageError=new MutableLiveData<>();
            loadSanPham();
        }
        return listSanPham;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    private void loadSanPham() {
        compositeDisposable.
                add(myRestaurantAPI.getSanPhamByIdDanhMuc(Common.API_KEY,
                        Common.selectCategprySelect.getIdDanhMuc(),Common.currentUser.getIdUser())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sanPhamModel -> {
                            if(sanPhamModel.isSuccess())
                            {
                                listSanPham.setValue(sanPhamModel.getResult());
                            }
                            else{
                                messageError.setValue(sanPhamModel.getMessage());
                            }

                        },throwable -> {
                            messageError.setValue(throwable.getMessage());
                        })
                );

    }



}
package etn.app.danghoc.shoppingclient.ui.chitietsanpham;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import etn.app.danghoc.shoppingclient.Common.Common;
import etn.app.danghoc.shoppingclient.Model.SanPham;

public class ChiTietSanPhamViewModel extends ViewModel {

    private MutableLiveData<SanPham> mutableLiveDataSanPham;

    public ChiTietSanPhamViewModel() {
            mutableLiveDataSanPham=new MutableLiveData<>();
    }

    public MutableLiveData<SanPham> getMutableLiveDataSanPham() {
        if(mutableLiveDataSanPham==null)
            mutableLiveDataSanPham=new MutableLiveData<>();
        mutableLiveDataSanPham.setValue(Common.selectSanPham);
        return mutableLiveDataSanPham;
    }

    public void setMutableLiveDataSanPham(MutableLiveData<SanPham> mutableLiveDataSanPham) {
        this.mutableLiveDataSanPham = mutableLiveDataSanPham;
    }
}
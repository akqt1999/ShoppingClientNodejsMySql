package etn.app.danghoc.shoppingclient.Callback;

import java.util.List;

import etn.app.danghoc.shoppingclient.Model.SanPham;

public interface ISanPhamCallbackListener {
    void onSanPhamLoadSuccess(List<SanPham> sanPhamModelList);
    void onSanPhamLoadFail(String message);
}

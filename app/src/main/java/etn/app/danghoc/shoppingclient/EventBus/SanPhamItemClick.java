package etn.app.danghoc.shoppingclient.EventBus;

import etn.app.danghoc.shoppingclient.Model.SanPham;

public class SanPhamItemClick {
    private boolean success;
    private SanPham sanPham;

    public SanPhamItemClick(boolean success, SanPham sanPham) {
        this.success = success;
        this.sanPham = sanPham;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
}

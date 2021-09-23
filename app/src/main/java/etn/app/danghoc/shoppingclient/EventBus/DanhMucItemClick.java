package etn.app.danghoc.shoppingclient.EventBus;

public class DanhMucItemClick {
    boolean success;
    int IdDanhMuc;

    public DanhMucItemClick(boolean success, int idDanhMuc) {
        this.success = success;
        IdDanhMuc = idDanhMuc;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getIdDanhMuc() {
        return IdDanhMuc;
    }

    public void setIdDanhMuc(int idDanhMuc) {
        IdDanhMuc = idDanhMuc;
    }
}

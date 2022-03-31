package etn.app.danghoc.shoppingclient.EventBus;

public class UpdateSanPhamAds {
    boolean success;
    int position;

    public UpdateSanPhamAds(boolean success, int position) {
        this.success = success;
        this.position = position;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

package etn.app.danghoc.shoppingclient.EventBus;

public class UpLoadImageSuccess {
    boolean success;

    public UpLoadImageSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

package etn.app.danghoc.shoppingclient.EventBus;

public class LoadCartAgain {
    boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public LoadCartAgain(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}

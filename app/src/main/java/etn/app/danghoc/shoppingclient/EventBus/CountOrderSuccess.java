package etn.app.danghoc.shoppingclient.EventBus;

public class CountOrderSuccess {
    boolean success;

    public CountOrderSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

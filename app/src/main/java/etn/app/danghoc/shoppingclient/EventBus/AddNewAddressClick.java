package etn.app.danghoc.shoppingclient.EventBus;

public class AddNewAddressClick {
    private boolean success;

    public AddNewAddressClick(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

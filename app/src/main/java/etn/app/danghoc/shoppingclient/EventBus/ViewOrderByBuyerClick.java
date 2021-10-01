package etn.app.danghoc.shoppingclient.EventBus;

public class ViewOrderByBuyerClick {
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ViewOrderByBuyerClick(boolean success) {
        this.success = success;
    }
}

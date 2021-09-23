package etn.app.danghoc.shoppingclient.EventBus;

public class UpdateStatusOrder {
    int status;
    int position;
    String idUser;

    public UpdateStatusOrder(int status, int position, String idSeller) {
        this.status = status;
        this.position = position;
        this.idUser = idSeller;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public UpdateStatusOrder(int status, int position) {
        this.status = status;
        this.position = position;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

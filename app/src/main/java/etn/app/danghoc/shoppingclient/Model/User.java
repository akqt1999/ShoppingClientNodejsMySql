package etn.app.danghoc.shoppingclient.Model;

public class User {

    private String IdUser, PhoneUser, NameUser, AddressUser;
    private double AmountMoney;
    int TrangThai;

    public User(String idUser, String phoneUser, String nameUser, String addressUser,int TrangThai) {
        IdUser = idUser;
        PhoneUser = phoneUser;
        NameUser = nameUser;
        AddressUser = addressUser;
        TrangThai=TrangThai;
    }

    public double getAmountMoney() {
        return AmountMoney;
    }

    public void setAmountMoney(double amountMoney) {
        AmountMoney = amountMoney;
    }

    public int getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(int trangThai) {
        TrangThai = trangThai;
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }

    public String getPhoneUser() {
        return PhoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        PhoneUser = phoneUser;
    }

    public String getNameUser() {
        return NameUser;
    }

    public void setNameUser(String nameUser) {
        NameUser = nameUser;
    }

    public String getAddressUser() {
        return AddressUser;
    }

    public void setAddressUser(String addressUser) {
        AddressUser = addressUser;
    }
}

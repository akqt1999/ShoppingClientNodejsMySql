package etn.app.danghoc.shoppingclient.Model;

public class Cart {
    String IdUser, TenSP,hinh,IdSeller;
    int SoLuong, IdSP;
    float Gia;


    boolean isChoose;

    public Cart(String idUser, String tenSP, String hinh, String idSeller, int soLuong, int idSP, float gia) {
        IdUser = idUser;
        TenSP = tenSP;
        this.hinh = hinh;
        IdSeller = idSeller;
        SoLuong = soLuong;
        IdSP = idSP;
        Gia = gia;
    }

    public String getIdSeller() {
        return IdSeller;
    }

    public void setIdSeller(String idSeller) {
        IdSeller = idSeller;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean check) {
        isChoose = check;
    }

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }

    public String getTenSP() {
        return TenSP;
    }

    public void setTenSP(String tenSP) {
        TenSP = tenSP;
    }

    public int getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(int soLuong) {
        SoLuong = soLuong;
    }

    public int getIdSP() {
        return IdSP;
    }

    public void setIdSP(int idSP) {
        IdSP = idSP;
    }

    public float getGia() {
        return Gia;
    }

    public void setGia(float gia) {
        Gia = gia;
    }
}

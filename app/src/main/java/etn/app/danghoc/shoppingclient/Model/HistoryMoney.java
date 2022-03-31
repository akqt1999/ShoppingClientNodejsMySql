package etn.app.danghoc.shoppingclient.Model;

import java.util.Date;

public class HistoryMoney {
    String IdUser;
    Date DateUpdateMoney;
    int trangThai;// 1 cộng tiền, -1 trừ tiền
    double Tien;

    public HistoryMoney(String idUser, Date dateUpdateMoney, int trangThai, double tien) {
        IdUser = idUser;
        DateUpdateMoney = dateUpdateMoney;
        this.trangThai = trangThai;
        Tien = tien;
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }

    public Date getDateUpdateMoney() {
        return DateUpdateMoney;
    }

    public void setDateUpdateMoney(Date dateUpdateMoney) {
        DateUpdateMoney = dateUpdateMoney;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public double getTien() {
        return Tien;
    }

    public void setTien(double tien) {
        Tien = tien;
    }
}

package etn.app.danghoc.shoppingclient.Model;

import java.util.Date;

public class Order {
    private int IdDonHang, IdSP;
    private int TrangThai;// 0 dang cho xac nhan ,1 da xac nhan ,2 da huy
    private Date NgayDat;
    private String IdUser, sdt, IdSeller, TenUser, DiaChi,TenSP,hinh,sdtSeller,nameSeller;
    private Double gia;



    public Order(int idDonHang, int idSP, int trangThai, Date ngayDat, String idUser, String sdt, String idSeller, String tenUser, String diaChi, String tenSP, String hinh, Double gia) {
        IdDonHang = idDonHang;
        IdSP = idSP;
        TrangThai = trangThai;
        NgayDat = ngayDat;
        IdUser = idUser;
        this.sdt = sdt;
        IdSeller = idSeller;
        TenUser = tenUser;
        DiaChi = diaChi;
        TenSP = tenSP;
        this.hinh = hinh;
        this.gia = gia;
    }

    public String getSdtSeller() {
        return sdtSeller;
    }

    public void setSdtSeller(String sdtSeller) {
        this.sdtSeller = sdtSeller;
    }

    public String getNameSeller() {
        return nameSeller;
    }

    public void setNameSeller(String nameSeller) {
        this.nameSeller = nameSeller;
    }

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    public String getTenSP() {
        return TenSP;
    }

    public void setTenSP(String tenSP) {
        TenSP = tenSP;
    }

    public int getIdDonHang() {
        return IdDonHang;
    }

    public void setIdDonHang(int idDonHang) {
        IdDonHang = idDonHang;
    }

    public int getIdSP() {
        return IdSP;
    }

    public void setIdSP(int idSP) {
        IdSP = idSP;
    }

    public int getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(int trangThai) {
        TrangThai = trangThai;
    }

    public Date getNgayDat() {
        return NgayDat;
    }

    public void setNgayDat(Date ngayDat) {
        NgayDat = ngayDat;
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getIdSeller() {
        return IdSeller;
    }

    public void setIdSeller(String idSeller) {
        IdSeller = idSeller;
    }

    public String getTenUser() {
        return TenUser;
    }

    public void setTenUser(String tenUser) {
        TenUser = tenUser;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public Double getGia() {
        return gia;
    }

    public void setGia(Double gia) {
        this.gia = gia;
    }
}

package etn.app.danghoc.shoppingclient.Model;

public class SanPham {
  private String   IdUser,TenSP,MoTa,hinh;
  private int IdSP,IdDanhMuc,trangthai;
  private float GiaSP;
  private String PhoneUser;

    public SanPham(String idUser, String tenSP, String moTa, String hinh, int idSP, int idDanhMuc, int trangthai, float giaSP) {
        IdUser = idUser;
        TenSP = tenSP;
        MoTa = moTa;
        this.hinh = hinh;
        IdSP = idSP;
        IdDanhMuc = idDanhMuc;
        this.trangthai = trangthai;
        GiaSP = giaSP;
    }

    public String getPhoneUser() {
        return PhoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        PhoneUser = phoneUser;
    }

    public int getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(int trangthai) {
        this.trangthai = trangthai;
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

    public String getMoTa() {
        return MoTa;
    }

    public void setMoTa(String moTa) {
        MoTa = moTa;
    }

    public String getHinh() {
        return hinh;
    }

    public void setHinh(String hinh) {
        this.hinh = hinh;
    }

    public int getIdSP() {
        return IdSP;
    }

    public void setIdSP(int idSP) {
        IdSP = idSP;
    }

    public int getIdDanhMuc() {
        return IdDanhMuc;
    }

    public void setIdDanhMuc(int idDanhMuc) {
        IdDanhMuc = idDanhMuc;
    }

    public float getGiaSP() {
        return GiaSP;
    }

    public void setGiaSP(float giaSP) {
        GiaSP = giaSP;
    }
}

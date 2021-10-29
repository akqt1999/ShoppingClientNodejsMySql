package etn.app.danghoc.shoppingclient.Model;

import java.util.List;

public class SanPham {
  private String   IdUser,TenSP,MoTa,hinh,listImage;
  private int IdSP,IdDanhMuc,trangthai;
  private float GiaSP;
  private String PhoneUser;
  private int ProvinceId;
  private List<LinkImageModel>listLinkImage;

    public SanPham(String idUser, String tenSP, String moTa, String hinh, String listImage, int idSP, int idDanhMuc, int trangthai, float giaSP, String phoneUser, int provinceId) {
        IdUser = idUser;
        TenSP = tenSP;
        MoTa = moTa;
        this.hinh = hinh;
        this.listImage = listImage;
        IdSP = idSP;
        IdDanhMuc = idDanhMuc;
        this.trangthai = trangthai;
        GiaSP = giaSP;
        PhoneUser = phoneUser;
        ProvinceId = provinceId;
    }

    public List<LinkImageModel> getListLinkImage() {
        return listLinkImage;
    }

    public void setListLinkImage(List<LinkImageModel> listLinkImage) {
        this.listLinkImage = listLinkImage;
    }

    public String getListImage() {
        return listImage;
    }

    public void setListImage(String listImage) {
        this.listImage = listImage;
    }

    public int getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(int provinceId) {
        ProvinceId = provinceId;
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

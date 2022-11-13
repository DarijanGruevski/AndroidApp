package com.example.individualproject;

public class User {

    String Name, email, password, Phone, Type;
    int VkupnoOceni;
    private int ZbirOceni;

    public User(){

    }

    public User(String name, String email, String password, String phone, String type,int zbirOceni, int vkOceni ) {
        this.Name = name;
        this.email = email;
        this.password = password;
        this.Phone = phone;
        this.Type = type;
        this.ZbirOceni = zbirOceni;
        this.VkupnoOceni = vkOceni;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public int getVkupnoOceni() {
        return VkupnoOceni;
    }

    public void setVkupnoOceni(int vkupnoOceni) {
        VkupnoOceni = vkupnoOceni;
    }

    public int getZbirOceni() {
        return ZbirOceni;
    }

    public void setZbirOceni(int zbirOceni) {
        ZbirOceni = zbirOceni;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
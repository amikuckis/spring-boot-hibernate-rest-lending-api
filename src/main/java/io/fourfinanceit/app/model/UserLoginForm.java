package io.fourfinanceit.app.model;

public class UserLoginForm {
    private String nationalIdentityNumber;
    private String password;

    public String getNationalIdentityNumber() {
        return nationalIdentityNumber;
    }

    public void setNationalIdentityNumber(String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

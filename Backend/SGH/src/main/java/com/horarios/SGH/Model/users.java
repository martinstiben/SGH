package com.horarios.SGH.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity(name = "users")
@Data
public class users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "userId")
        private int userId;

    @Column(name = "userName")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
        private String userName;

    @Column(name = "password")
    @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres")
        private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
        private Role role;

    @Column(name = "photoData")
    @Lob
    private byte[] photoData;

    @Column(name = "photoContentType", length = 100)
    private String photoContentType;

    @Column(name = "photoFileName", length = 255)
    private String photoFileName;

    public users(int userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    public users() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getPhotoData() {
        return photoData;
    }

    public void setPhotoData(byte[] photoData) {
        this.photoData = photoData;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }
}
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

    @Column(name = "name")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        private String name;

    @Column(name = "email")
    @Size(min = 3, max = 100, message = "El correo electrónico debe tener entre 3 y 100 caracteres")
        private String email;

    @Column(name = "password")
    @Size(min = 8, max = 12, message = "La contraseña debe tener entre 8 y 12 caracteres")
        private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
        private Role role;

    @Column(name = "verification_code")
        private String verificationCode;

    @Column(name = "code_expiration")
        private java.time.LocalDateTime codeExpiration;

    @Column(name = "photoData", columnDefinition = "LONGBLOB")
    @Lob
    private byte[] photoData;

    @Column(name = "photoContentType", length = 100)
    private String photoContentType;

    @Column(name = "photoFileName", length = 255)
    private String photoFileName;

    public users(int userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Para compatibilidad con autenticación, getUserName retorna email
    public String getUserName() {
        return email;
    }

    public void setUserName(String userName) {
        this.email = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public java.time.LocalDateTime getCodeExpiration() {
        return codeExpiration;
    }

    public void setCodeExpiration(java.time.LocalDateTime codeExpiration) {
        this.codeExpiration = codeExpiration;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
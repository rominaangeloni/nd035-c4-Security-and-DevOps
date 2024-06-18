package com.wallapop.iam.keycloak.extensions.monolithusers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
        @NamedQuery(name="getUserByEmail",
                    query="select au from AuthUser au where au.email = :email"),
})
@Entity
@Table(name = "user_credentials")
public class AuthUser {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
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
}

package com.wallapop.iam.keycloak.extensions.monolithusers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@NamedQueries({
        @NamedQuery(name="getProUserByEmail",
                    query="select u from MonolithUser u JOIN u.userPerks p WHERE u.email = :email AND p.type = 'USER_PROFILE_FEATURED' AND p.periodEnd > CURRENT_TIMESTAMP()"),
})
@Entity
@Table(name = "usr_usr")
public class MonolithUser {
    @Id
    @Column(name = "usrId")
    private Long userId;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "userEmailAddress")
    private String email;
    @Column(name = "password_")
    private String password;

    @OneToMany(mappedBy = "user") // Define the relationship with UserPerks
    private Set<MonolithUserPerk> userPerks;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<MonolithUserPerk> getUserPerks() {
        return userPerks;
    }

    public void setUserPerks(Set<MonolithUserPerk> userPerks) {
        this.userPerks = userPerks;
    }
}

package com.wallapop.iam.keycloak.extensions.monolithusers;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_perks")
public class MonolithUserPerk {

    @EmbeddedId
    private UserPerkId id;

    @ManyToOne
    @MapsId("usrId")
    @JoinColumn(name = "user_id")
    private MonolithUser user;

    @Column(name = "type")
    private String type;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "period_end")
    private Timestamp periodEnd;

    public UserPerkId getId() {
        return id;
    }

    public void setId(UserPerkId id) {
        this.id = id;
    }

    public MonolithUser getUser() {
        return user;
    }

    public void setUser(MonolithUser user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Timestamp getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Timestamp periodEnd) {
        this.periodEnd = periodEnd;
    }

    @Embeddable
    public static class UserPerkId implements Serializable {
        private Long perkId;
        private Long usrId;
        private Long categoryId;

        public Long getPerkId() {
            return perkId;
        }

        public void setPerkId(Long perkId) {
            this.perkId = perkId;
        }

        public Long getUsrId() {
            return usrId;
        }

        public void setUsrId(Long usrId) {
            this.usrId = usrId;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserPerkId that = (UserPerkId) o;
            return Objects.equals(perkId, that.perkId) &&
                   Objects.equals(usrId, that.usrId) &&
                   Objects.equals(categoryId, that.categoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(perkId, usrId, categoryId);
        }
    }
}

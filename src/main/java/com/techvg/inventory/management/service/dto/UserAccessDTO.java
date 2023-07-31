package com.techvg.inventory.management.service.dto;

import com.techvg.inventory.management.domain.enumeration.AccessLevel;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.techvg.inventory.management.domain.UserAccess} entity.
 */
public class UserAccessDTO implements Serializable {

    private Long id;

    private AccessLevel level;

    private Long accessId;

    private String lastModified;

    private String lastModifiedBy;

    private Long securityUserId;

    private String securityUserLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccessLevel getLevel() {
        return level;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    public Long getAccessId() {
        return accessId;
    }

    public void setAccessId(Long accessId) {
        this.accessId = accessId;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getSecurityUserId() {
        return securityUserId;
    }

    public void setSecurityUserId(Long securityUserId) {
        this.securityUserId = securityUserId;
    }

    public String getSecurityUserLogin() {
        return securityUserLogin;
    }

    public void setSecurityUserLogin(String securityUserLogin) {
        this.securityUserLogin = securityUserLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccessDTO)) {
            return false;
        }

        return id != null && id.equals(((UserAccessDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAccessDTO{" +
            "id=" + getId() +
            ", level='" + getLevel() + "'" +
            ", accessId=" + getAccessId() +
            ", lastModified='" + getLastModified() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", securityUserId=" + getSecurityUserId() +
            ", securityUserLogin='" + getSecurityUserLogin() + "'" +
            "}";
    }
}

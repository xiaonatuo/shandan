package com.keyware.shandan.beans;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * GrantedAuthorityDTO
 *
 * @author Administrator
 * @since 2021/11/30
 */
@Getter
@Setter
public class GrantedAuthorityDTO implements GrantedAuthority {
    private static final long serialVersionUID = -4932400493701408751L;

    private String role;

    public GrantedAuthorityDTO(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof GrantedAuthorityDTO) {
            return role.equals(((GrantedAuthorityDTO) obj).role);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.role.hashCode();
    }

    @Override
    public String toString() {
        return this.role;
    }
}

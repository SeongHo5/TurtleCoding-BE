package net.turtlecoding.damgo.account.service;

import net.turtlecoding.damgo.account.entity.Account;
import net.turtlecoding.damgo.account.enums.UserRole;
import net.turtlecoding.damgo.common.exception.NotFoundException;
import net.turtlecoding.damgo.common.exception.enums.ExceptionStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class AccountDetails implements UserDetails {

    private final Account account;

    private String email;

    public AccountDetails(Account account) {
        if (account == null) {
            throw new NotFoundException(ExceptionStatus.NOT_FOUND_ACCOUNT);
        }
        this.account = account;
        this.email = account.getEmail();
    }

    public Account getAccount() {
        return account;
    }

    /**
     * {@link Account} 정보의 {@link UserRole}에 따라 권한을 부여하고, 컬렉션에 담아 반환한다.
     * @return authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole userRole = account.getRole();
        String authority = userRole.getAuthority();
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

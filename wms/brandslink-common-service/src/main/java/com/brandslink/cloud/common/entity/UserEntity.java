package com.brandslink.cloud.common.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * springsecurity用户实体类
 *
 * @ClassName UserEntity
 * @Author tianye
 * @Date 2019/6/13 16:34
 * @Version 1.0
 */
public class UserEntity implements UserDetails {

//    private static final long serialVersionUID = -839022911714555620L;

    private String username;
    private String password;
    private Integer enabled;
    private Collection<? extends GrantedAuthority> authorities;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * 获取当前用户所具有的角色
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 获取当前用户的密码
     *
     * @return
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 获取当前用户的用户名
     *
     * @return
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 当前用户账号是否过期
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 当前用户是否没有被锁定
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 当前用户授权是否过期
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 当前用户账号是否激活
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return enabled == 0;
    }
}

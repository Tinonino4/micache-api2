package com.micache.mi_cache.user.domain;

import com.micache.mi_cache.model.UserProfile;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String role;

    // -----------------------------------------------------------------
    // 1. CONSTRUCTORES (Reemplaza @NoArgsConstructor y @AllArgsConstructor)
    // -----------------------------------------------------------------

    // Constructor vacío requerido por JPA
    public User() {
    }

    // Constructor con todos los argumentos
    public User(Long id, String email, String password, boolean active, String role, UserProfile profile) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
        this.role = role;
    }

    // -----------------------------------------------------------------
    // 2. GETTERS Y SETTERS (Reemplaza parte de @Data)
    // -----------------------------------------------------------------

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // -----------------------------------------------------------------
    // 3. IMPLEMENTACIÓN DE USER DETAILS
    // -----------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // -----------------------------------------------------------------
    // 4. EQUALS, HASHCODE Y TOSTRING (Reemplaza resto de @Data)
    // -----------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Usamos email como clave de negocio ya que es unique=true
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", role='" + role + '\'' +
                // Importante: No incluimos 'profile' para evitar ciclo infinito
                '}';
    }

    // -----------------------------------------------------------------
    // 5. PATRÓN BUILDER (Reemplaza @Builder)
    // -----------------------------------------------------------------

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String email;
        private String password;
        private boolean active;
        private String role;
        private UserProfile profile;

        UserBuilder() {
        }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public UserBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserBuilder profile(UserProfile profile) {
            this.profile = profile;
            return this;
        }

        public User build() {
            return new User(id, email, password, active, role, profile);
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", email=" + this.email + ", password=" + this.password + ", active=" + this.active + ", role=" + this.role + ", profile=" + this.profile + ")";
        }
    }
}
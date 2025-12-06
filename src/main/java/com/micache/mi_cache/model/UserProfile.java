package com.micache.mi_cache.model;

import com.micache.mi_cache.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^\\+?[0-9 .\\-]{7,20}$", message = "Número de teléfono inválido")
    private String phoneNumber;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String jobTitle;

    @Size(max = 200)
    private String education;

    @Size(max = 10)
    private String zipcode;

    private LocalDate birthday;

    @Column(nullable = false)
    private String photo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // --- 1. Constructores ---

    public UserProfile() {
    }

    public UserProfile(Long id, User user, String name, String email, String phoneNumber, String city, String jobTitle, String education, String zipcode, LocalDate birthday, String photo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.jobTitle = jobTitle;
        this.education = education;
        this.zipcode = zipcode;
        this.birthday = birthday;
        this.photo = photo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- 2. Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- 3. Métodos JPA Lifecycle ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- 4. equals y hashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(city, that.city) &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(education, that.education) &&
                Objects.equals(zipcode, that.zipcode) &&
                Objects.equals(birthday, that.birthday) &&
                Objects.equals(photo, that.photo) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, name, email, phoneNumber, city, jobTitle, education, zipcode, birthday, photo, createdAt, updatedAt);
    }

    // --- 5. toString ---

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) + // Evitamos recursividad imprimiendo solo el ID
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", city='" + city + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", education='" + education + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", birthday=" + birthday +
                ", photo='" + photo + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // --- 6. Builder Pattern ---

    public static UserProfileBuilder builder() {
        return new UserProfileBuilder();
    }

    public static class UserProfileBuilder {
        private Long id;
        private User user;
        private String name;
        private String email;
        private String phoneNumber;
        private String city;
        private String jobTitle;
        private String education;
        private String zipcode;
        private LocalDate birthday;
        private String photo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        UserProfileBuilder() {
        }

        public UserProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserProfileBuilder user(User user) {
            this.user = user;
            return this;
        }

        public UserProfileBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserProfileBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserProfileBuilder city(String city) {
            this.city = city;
            return this;
        }

        public UserProfileBuilder jobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public UserProfileBuilder education(String education) {
            this.education = education;
            return this;
        }

        public UserProfileBuilder zipcode(String zipcode) {
            this.zipcode = zipcode;
            return this;
        }

        public UserProfileBuilder birthday(LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public UserProfileBuilder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public UserProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(id, user, name, email, phoneNumber, city, jobTitle, education, zipcode, birthday, photo, createdAt, updatedAt);
        }

        public String toString() {
            return "UserProfile.UserProfileBuilder(id=" + this.id + ", name=" + this.name + ", email=" + this.email + ")";
        }
    }
}
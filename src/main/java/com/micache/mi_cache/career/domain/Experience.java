package com.micache.mi_cache.career.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.micache.mi_cache.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "experiences")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String companyName;

    private String department;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate finishDate;

    @Column(columnDefinition = "TEXT")
    private String functions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // --- 1. Constructores (@NoArgsConstructor y @AllArgsConstructor) ---

    public Experience() {
    }

    public Experience(Long id, User user, String companyName, String department, String position, LocalDate startDate, LocalDate finishDate, String functions, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.functions = functions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- 2. Getters y Setters (@Data incluye @Getter y @Setter) ---

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public String getFunctions() {
        return functions;
    }

    public void setFunctions(String functions) {
        this.functions = functions;
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

    // --- 3. Métodos del ciclo de vida JPA (Sin cambios) ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- 4. equals y hashCode (@Data los incluye) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Experience that = (Experience) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(companyName, that.companyName) &&
                Objects.equals(department, that.department) &&
                Objects.equals(position, that.position) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(finishDate, that.finishDate) &&
                Objects.equals(functions, that.functions) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, companyName, department, position, startDate, finishDate, functions, createdAt, updatedAt);
    }

    // --- 5. toString (@Data lo incluye) ---

    @Override
    public String toString() {
        return "Experience{" +
                "id=" + id +
                ", user=" + user + // Cuidado: Posible recursividad si User tiene toString con Experience
                ", companyName='" + companyName + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                ", functions='" + functions + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // --- 6. Builder Pattern (@Builder) ---

    public static ExperienceBuilder builder() {
        return new ExperienceBuilder();
    }

    public static class ExperienceBuilder {
        private Long id;
        private User user;
        private String companyName;
        private String department;
        private String position;
        private LocalDate startDate;
        private LocalDate finishDate;
        private String functions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        ExperienceBuilder() {
        }

        public ExperienceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExperienceBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ExperienceBuilder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public ExperienceBuilder department(String department) {
            this.department = department;
            return this;
        }

        public ExperienceBuilder position(String position) {
            this.position = position;
            return this;
        }

        public ExperienceBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public ExperienceBuilder finishDate(LocalDate finishDate) {
            this.finishDate = finishDate;
            return this;
        }

        public ExperienceBuilder functions(String functions) {
            this.functions = functions;
            return this;
        }

        public ExperienceBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ExperienceBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Experience build() {
            return new Experience(id, user, companyName, department, position, startDate, finishDate, functions, createdAt, updatedAt);
        }

        public String toString() {
            return "Experience.ExperienceBuilder(id=" + this.id + ", user=" + this.user + ", companyName=" + this.companyName + ", department=" + this.department + ", position=" + this.position + ", startDate=" + this.startDate + ", finishDate=" + this.finishDate + ", functions=" + this.functions + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
        }
    }
}
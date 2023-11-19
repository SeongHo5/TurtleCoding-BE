package net.turtlecoding.damgo.account.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.turtlecoding.damgo.account.enums.UserRole;
import org.hibernate.annotations.SQLDelete;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "members")
@SQLDelete(sql = "UPDATE members SET deleted_at = current_timestamp WHERE id = ?")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Email
    @Size(max = 255)
    @NotNull
    @Column(name = "contact", nullable = false)
    private String contact;

    @Size(max = 255)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;


    @Builder
    public Account(Long id, String name, String email, String password, String contact, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role;
    }


}

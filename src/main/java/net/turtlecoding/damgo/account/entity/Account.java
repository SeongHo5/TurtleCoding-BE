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
    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 255)
    @NotNull
    @Column(name = "contact", nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Builder
    public Account(String name, String email, String password, String contact, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role;
    }


}

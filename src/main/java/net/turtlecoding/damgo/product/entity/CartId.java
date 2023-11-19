package net.turtlecoding.damgo.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.turtlecoding.damgo.account.entity.Account;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Value
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class CartId implements Serializable {
    private static final long serialVersionUID = -8131047912203433099L;

    @NotNull
    @Column(name = "_id", nullable = false)
    Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    Long userId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CartId entity = (CartId) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }

    @Builder
    public CartId(Account account) {
        // DB 컬럼에서 Auto Increment 를 사용하므로 id는 null로 설정
        this.id = null;
        this.userId = account.getId();
    }

}

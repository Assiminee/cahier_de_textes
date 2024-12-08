package upf.pjt.cahier_de_textes.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    public Role() {}

    public Role(RoleEnum role) {
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", unique = true, nullable = false)
    @Getter
    @Setter
    private RoleEnum role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @Getter
    @Setter
    @JsonBackReference
    private List<User> users = new ArrayList<>();

    @Override
    public String getAuthority() {
        return getRole().toString();
    }
}

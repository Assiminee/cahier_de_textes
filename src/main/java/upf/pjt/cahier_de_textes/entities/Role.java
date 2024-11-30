package upf.pjt.cahier_de_textes.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
public class Role {
    public Role() {}

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
}

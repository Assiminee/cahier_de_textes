package upf.pjt.cahier_de_textes.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity @Getter @Setter
@Table(name = "objectif")
public class Objectif {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "contenue")
    private String contenue;

    @Column(name = "atteint")
    private boolean atteint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entree")
    private Entree entree;

    @Override
    public String toString() {
        return "Objectif{" +
                "id=" + id +
                ", contenue='" + contenue + '\'' +
                ", atteint=" + atteint +
                ", entree=" + (entree == null ? "null" : entree.getId()) +
                '}';
    }
}

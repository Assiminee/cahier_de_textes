package upf.pjt.cahier_de_textes.dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import upf.pjt.cahier_de_textes.dao.entities.enumerations.NatureSeance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name = "entree")
public class Entree {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "seance", nullable = false)
    private int seance;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "nature")
    @Enumerated(EnumType.STRING)
    private NatureSeance nature;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "entree", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Objectif> objectifs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cahier")
    private Cahier cahier;

    @Override
    public String toString() {
        return "Entree{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", seance=" + seance +
                ", date=" + date +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", nature=" + nature +
                ", description='" + description + '\'' +
                ", objectifs=" + objectifs +
                ", cahier=" + (cahier == null ? "null" : cahier.getId()) +
                '}';
    }

    public void addObjectif(Objectif objectif) {
        if (this.objectifs == null)
            this.objectifs = new ArrayList<>();

        this.objectifs.add(objectif);
    }
}

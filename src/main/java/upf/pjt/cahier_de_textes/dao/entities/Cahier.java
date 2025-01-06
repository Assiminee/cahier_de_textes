package upf.pjt.cahier_de_textes.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity @Getter @Setter
@Table(name = "cahier")
public class Cahier {
    public Cahier() {
        this.archived = false;

        if (this.entrees == null)
            this.entrees = new ArrayList<>();
    }

    public Cahier(Affectation affectation) {
        this.archived = false;

        if (this.entrees == null)
            this.entrees = new ArrayList<>();

        this.filiere = affectation.getFiliere().getIntitule();
        this.module = affectation.getModule().getIntitule();
        this.professeur = affectation.getProf().getFullName();
        this.niveau = affectation.getNiveau();
        this.semestre = affectation.getSemestre();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "filiere")
    private String filiere;

    @Column(name = "module")
    private String module;

    @Column(name = "professeur")
    private String professeur;

    @Column(name = "niveau")
    private int niveau;

    @Column(name = "semestre")
    private int semestre;

    @Column(name = "archive")
    private boolean archived;

    @OneToMany(mappedBy = "cahier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Entree> entrees;

    @OneToOne(mappedBy = "cahier")
    private Affectation affectation;

    @Override
    public String toString() {
        return "Cahier{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", filiere='" + filiere + '\'' +
                ", module='" + module + '\'' +
                ", professeur='" + professeur + '\'' +
                ", archived=" + archived +
                ", entrees=" + entrees +
                '}';
    }

    public void addEntree(Entree entree) {
        if (this.entrees == null)
            this.entrees = new ArrayList<>();

        this.entrees.add(entree);
    }
}

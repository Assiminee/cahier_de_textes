package upf.pjt.cahier_de_textes.dao.entities.enumerations;

import lombok.Getter;

public enum Jour {
    LUN("Lundi"),
    MAR("Mardi"),
    MER("Mercredi"),
    JEU("Jeudi"),
    VEN("Vendredi"),
    SAM("Samedi");

    @Getter
    private String jourComplet;

    Jour(String jourComplet) {
        this.jourComplet = jourComplet;
    }
}

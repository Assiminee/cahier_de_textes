package upf.pjt.cahier_de_textes.dao.entities.enumerations;

import lombok.Getter;

public enum Diplome {
    CP("Classes Préparatoires", 2),
    LC("Licence", 3),
    CI("Cycle d'Ingenieur", 5),
    MA("Master", 5);

    @Getter
    private final String fullName;
    @Getter
    private final int nombreAnnees;

    Diplome(String fullName, int nombreAnnees) {
        this.fullName = fullName;
        this.nombreAnnees = nombreAnnees;
    }

    public static Diplome getDiplome(String nom) {
        try {
            return Diplome.valueOf(nom.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}


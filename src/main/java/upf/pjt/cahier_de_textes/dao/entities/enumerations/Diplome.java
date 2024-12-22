package upf.pjt.cahier_de_textes.dao.entities.enumerations;

import lombok.Getter;

public enum Diplome {
    CP("Classes Préparatoires"),
    LC("Licence"),
    CI("Cycle d'Ingenieur"),
    MA("Master");

    @Getter
    private final String fullName;

    Diplome(String fullName) {
        this.fullName = fullName;
    }
}


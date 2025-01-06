package upf.pjt.cahier_de_textes.dao.entities.enumerations;
public enum RoleEnum {
    ROLE_PROF,
    ROLE_ADMIN,
    ROLE_SS,
    ROLE_SP;


    @Override
    public String toString() {
        return name();
    }
}

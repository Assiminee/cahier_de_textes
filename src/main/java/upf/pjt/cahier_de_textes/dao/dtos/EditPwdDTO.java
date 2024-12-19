package upf.pjt.cahier_de_textes.dao.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EditPwdDTO {
    private String oldPassword;
    private String newPassword;
}

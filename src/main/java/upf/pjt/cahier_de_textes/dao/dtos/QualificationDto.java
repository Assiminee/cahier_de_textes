package upf.pjt.cahier_de_textes.dao.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class QualificationDto {

    @NotBlank
    private String intitule;

    @NotNull
    private LocalDate dateObtention;
}

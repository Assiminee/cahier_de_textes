/*
package upf.pjt.cahier_de_textes.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import upf.pjt.cahier_de_textes.dao.ProfesseurRepository;
import upf.pjt.cahier_de_textes.dao.RoleRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.entities.Professeur;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;
import upf.pjt.cahier_de_textes.models.CustomUserDetails;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(name = "UserEndpoint", path = "/api/users", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PUT})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

 */
/*   @GetMapping()
    public ResponseEntity<List<User>> getUsers(
         @RequestParam(required = false) String field,
            @RequestParam(required = false) String keyword
//            @RequestParam(required = false) int page,
//            @RequestParam(required = false) int count
    ) {
//        String hql = "SELECT u FROM User u WHERE u." + field + " LIKE :keyword";
//        TypedQuery<User> query = entityManager.createQuery(hql, User.class);
////        query.setParameter("field", "%" + field + "%");
//        query.setParameter("keyword", "%" + keyword + "%");
//        query.setFirstResult((page - 1) * count);
//        query.setMaxResults(count);
//        return query.getResultList();
        String upperCaseKeyword = keyword.toUpperCase();
        RoleEnum roleEnum = RoleEnum.valueOf(upperCaseKeyword);
        List<User> users = userRepository.findAllByRole(roleRepository.findOneByRole(roleEnum));
        return new ResponseEntity<>(users, HttpStatus.OK);

    }*//*

 private ProfesseurRepository professeurRepository;

    */
/*@Autowired
    public UserController(ProfesseurRepository professeurRepository) {
        this.professeurRepository = professeurRepository;
    }*//*

    @GetMapping("/Admin/users")
    public String profile(Model model) {
       */
/* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            User user = userDetails.getUser();

            if (Objects.equals(user.getRole().getAuthority(), RoleEnum.ROLE_PROF.name())) {
                Professeur prof = professeurRepository.getReferenceById(user.getId());
                model.addAttribute("user", prof);
            } else {
                model.addAttribute("user", user);
            }
        } else {
            model.addAttribute("user", null); // Handle case where userDetails is not present
        }
*//*

        return "Admin/users";
    }


}
*/

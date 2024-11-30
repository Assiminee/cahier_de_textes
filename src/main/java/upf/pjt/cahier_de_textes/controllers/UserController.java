package upf.pjt.cahier_de_textes.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import upf.pjt.cahier_de_textes.dao.RoleRepository;
import upf.pjt.cahier_de_textes.dao.UserRepository;
import upf.pjt.cahier_de_textes.entities.User;
import upf.pjt.cahier_de_textes.entities.enumerations.RoleEnum;

import java.util.List;

@RestController
@RequestMapping(name = "UserEndpoint", path = "/api/users", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PUT})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping()
    public List<User> getUsers(
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
        return userRepository.findAllByRole(roleRepository.findOneByRole(roleEnum));
    }
}

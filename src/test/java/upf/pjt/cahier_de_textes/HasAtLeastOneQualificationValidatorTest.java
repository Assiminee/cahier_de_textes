package upf.pjt.cahier_de_textes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import upf.pjt.cahier_de_textes.dao.entities.Qualification;
import upf.pjt.cahier_de_textes.dao.entities.validation_annotations.HasAtLeastOneQualificationValidator;

import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HasAtLeastOneQualificationValidatorTest {

    private HasAtLeastOneQualificationValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new HasAtLeastOneQualificationValidator();
        context = Mockito.mock(ConstraintValidatorContext.class); // Mocking the context
    }

    @Test
    void testValidListWithOneQualification() {
        // Arrange: Create a valid list with one Qualification
        List<Qualification> qualifications = new ArrayList<>();
        qualifications.add(new Qualification());

        // Act: Call the isValid method
        boolean isValid = validator.isValid(qualifications, context);

        // Assert: The list is valid
        assertTrue(isValid, "A list with one qualification should be valid");
    }

    @Test
    void testValidListWithMultipleQualifications() {
        // Arrange: Create a valid list with multiple Qualifications
        List<Qualification> qualifications = new ArrayList<>();
        qualifications.add(new Qualification());
        qualifications.add(new Qualification());

        // Act: Call the isValid method
        boolean isValid = validator.isValid(qualifications, context);

        // Assert: The list is valid
        assertTrue(isValid, "A list with multiple qualifications should be valid");
    }

    @Test
    void testEmptyListIsInvalid() {
        // Arrange: Create an empty list
        List<Qualification> qualifications = new ArrayList<>();

        // Act: Call the isValid method
        boolean isValid = validator.isValid(qualifications, context);

        // Assert: The list is invalid
        assertFalse(isValid, "An empty list should be invalid");
    }

    @Test
    void testNullListIsValid() {
        // Arrange: A null list
        List<Qualification> qualifications = null;

        // Act: Call the isValid method
        boolean isValid = validator.isValid(qualifications, context);

        // Assert: A null list is considered valid
        assertTrue(isValid, "A null list should be valid");
    }

    @Test
    void testListWithNullElementIsInvalid() {
        // Arrange: A list with a null element
        List<Qualification> qualifications = new ArrayList<>();
        qualifications.add(null);

        // Act: Call the isValid method
        boolean isValid = validator.isValid(qualifications, context);

        // Assert: The list is invalid
        assertFalse(isValid, "A list with a null element should be invalid");
    }
}

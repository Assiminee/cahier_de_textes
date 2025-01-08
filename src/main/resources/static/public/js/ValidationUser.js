document.addEventListener("DOMContentLoaded", function () {
    // --- Define the validation function first ---
    const validateUserForm = (form) => {
        let isValid = true;

        // Regex Patterns
        const nomRegex = /^[a-zA-ZÀ-ÿ]+([ '-][a-zA-ZÀ-ÿ]+)*$/;
        const cinRegex = /^[A-Za-z]{1,2}[0-9]{4,6}$/;
        const telephoneRegex = /^\+212[6-7][0-9]{8}$/;
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        // Nom
        const nomInput = form.querySelector('[name="nom"]');
        const nomError = form.querySelector('#nom-error');
        if (nomInput && !nomRegex.test(nomInput.value.trim())) {
            nomError.classList.remove("hidden");
            isValid = false;
        } else if (nomError) {
            nomError.classList.add("hidden");
        }

        // Prénom
        const prenomInput = form.querySelector('[name="prenom"]');
        const prenomError = form.querySelector('#prenom-error');
        if (prenomInput && !nomRegex.test(prenomInput.value.trim())) {
            prenomError.classList.remove("hidden");
            isValid = false;
        } else if (prenomError) {
            prenomError.classList.add("hidden");
        }

        // Email
        const emailInput = form.querySelector('[name="email"]');
        const emailError = form.querySelector('#email-error');
        if (emailInput && !emailInput.validity.valid) {
            emailError.textContent = "Invalid email format.";
            emailError.classList.remove("hidden");
            isValid = false;
        } else if (emailError) {
            emailError.classList.add("hidden");
        }

        // Date de Naissance
        const dateNaissanceInput = form.querySelector('[name="dateNaissance"]');
        const dateNaissanceError = form.querySelector('#dateNaissance-error');
        if (dateNaissanceInput) {
            const today = new Date();
            const birthDate = new Date(dateNaissanceInput.value);
            let age = today.getFullYear() - birthDate.getFullYear();
            const monthDiff = today.getMonth() - birthDate.getMonth();
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }
            if (isNaN(age) || age < 18) {
                dateNaissanceError.classList.remove("hidden");
                isValid = false;
            } else if (dateNaissanceError) {
                dateNaissanceError.classList.add("hidden");
            }
        }

        // CIN
        const cinInput = form.querySelector('[name="cin"]');
        const cinError = form.querySelector('#cin-error');
        if (cinInput && !cinRegex.test(cinInput.value.trim())) {
            cinError.classList.remove("hidden");
            isValid = false;
        } else if (cinError) {
            cinError.classList.add("hidden");
        }

        // Téléphone
        const telephoneInput = form.querySelector('[name="telephone"]');
        const telephoneError = form.querySelector('#telephone-error');
        if (telephoneInput && !telephoneRegex.test(telephoneInput.value.trim())) {
            telephoneError.classList.remove("hidden");
            isValid = false;
        } else if (telephoneError) {
            telephoneError.classList.add("hidden");
        }

        // Password and Confirm Password for Add Mode
        const passwordAdd = form.querySelector("#passwordAdd");
        const confirmPasswordAdd = form.querySelector("#confirmPasswordAdd");
        if (passwordAdd && confirmPasswordAdd) {
            const passwordAddError = form.querySelector("#passwordAdd-error");
            const confirmPasswordAddError = form.querySelector("#confirmPasswordAdd-error");

            if (!passwordRegex.test(passwordAdd.value)) {
                passwordAddError.classList.remove("hidden");
                isValid = false;
            } else {
                passwordAddError.classList.add("hidden");
            }

            if (passwordAdd.value !== confirmPasswordAdd.value) {
                confirmPasswordAddError.classList.remove("hidden");
                isValid = false;
            } else {
                confirmPasswordAddError.classList.add("hidden");
            }
        }

        // Password and Confirm Password for Add Mode
        const password= form.querySelector("#password");
        const confirmPassword= form.querySelector("#confirmPassword");
        if (password && confirmPassword) {
            const passwordError = form.querySelector("#password-error");
            const confirmPasswordError = form.querySelector("#confirmPassword-error");

            if (password.value.trim() !== "" || confirmPassword.value.trim() !== "") {
                if (!passwordRegex.test(password.value)) {
                    passwordError.classList.remove("hidden");
                    isValid = false;
                } else {
                    passwordError.classList.add("hidden");
                }

                if (password.value !== confirmPassword.value) {
                    confirmPasswordError.classList.remove("hidden");
                    isValid = false;
                } else {
                    confirmPasswordError.classList.add("hidden");
                }
            }
        }

        // Password validation for Edit Mode
        const passwordEdit = form.querySelector("#passwordEdit");
        const confirmPasswordEdit = form.querySelector("#confirmPasswordEdit");
        if (passwordEdit && confirmPasswordEdit) {
            const passwordEditError = form.querySelector("#passwordEdit-error");
            const confirmPasswordEditError = form.querySelector("#confirmPasswordEdit-error");

            if (passwordEdit.value.trim() !== "") {
                if (!passwordRegex.test(passwordEdit.value)) {
                    passwordEditError.classList.remove("hidden");
                    isValid = false;
                } else {
                    passwordEditError.classList.add("hidden");
                }

                if (passwordEdit.value !== confirmPasswordEdit.value) {
                    confirmPasswordEditError.classList.remove("hidden");
                    isValid = false;
                } else {
                    confirmPasswordEditError.classList.add("hidden");
                }
            } else {
                passwordEditError.classList.add("hidden");
                confirmPasswordEditError.classList.add("hidden");
            }
        }

        return isValid;
    };
    window.validateUserForm = validateUserForm;

    // --- Now attach event listeners after defining the function ---
    const addUserForm = document.getElementById("addUserForm");
    if (addUserForm) {
        addUserForm.addEventListener("submit", function (event) {
            if (!validateUserForm(addUserForm)) {
                event.preventDefault();
            }
        });
    }

    const userModalForm = document.getElementById("userModalForm");
    if (userModalForm) {
        userModalForm.addEventListener("submit", function (event) {
            if (!validateUserForm(userModalForm)) {
                event.preventDefault();
            }
        });
    }

    const addProfForm = document.getElementById("addProfForm");
    if (addProfForm) {
        addProfForm.addEventListener("submit", function (event) {
            if (!validateUserForm(addProfForm)) {
                event.preventDefault();
            }
        });
    }

    document.querySelectorAll('[id^="editUserForm-"]').forEach((form) => {
        form.addEventListener("submit", function (event) {
            if (!validateUserForm(form)) {
                event.preventDefault();
            }
        });
    });
});

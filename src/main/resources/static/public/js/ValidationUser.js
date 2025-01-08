document.addEventListener("DOMContentLoaded", function () {
    // Attach validation logic to "addUserForm"
    const addUserForm = document.getElementById("addUserForm");
    if (addUserForm) {
        addUserForm.addEventListener("submit", function (event) {
            const isValid = validateUserForm(addUserForm);

            if (!isValid) {
                event.preventDefault(); // Prevent submission if validation fails
            }
        });
    }

    // Attach validation logic to "addProfForm"
    const addProfForm = document.getElementById("addProfForm");
    if (addProfForm) {
        addProfForm.addEventListener("submit", function (event) {
            const isValid = validateUserForm(addProfForm);

            if (!isValid) {
                event.preventDefault(); // Prevent submission if validation fails
            }
        });
    }

    // Attach to edit modals dynamically
    document.querySelectorAll('[id^="editUserForm-"]').forEach((form) => {
        form.addEventListener("submit", function (event) {
            const isValid = validateUserForm(form);

            if (!isValid) {
                event.preventDefault(); // Prevent submission if validation fails
            }
        });
    });

    // Validation function for both Add and Edit forms
    const validateUserForm = (form) => {
        let isValid = true;

        // Regex Patterns
        const nomRegex = /^[a-zA-ZÀ-ÿ]+([ '-][a-zA-ZÀ-ÿ]+)*$/;
        const cinRegex = /^[A-Za-z]{1,2}[0-9]{4,6}$/;
        const telephoneRegex = /^\+212[6-7][0-9]{8}$/;
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        const mode = form.getAttribute("data-mode");

        // Nom
        console.log(form);
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

        // Telephone
        const telephoneInput = form.querySelector('[name="telephone"]');
        const telephoneError = form.querySelector('#telephone-error');
        if (telephoneInput && !telephoneRegex.test(telephoneInput.value.trim())) {
            telephoneError.classList.remove("hidden");
            isValid = false;
        } else if (telephoneError) {
            telephoneError.classList.add("hidden");
        }

        const passwordAdd = document.getElementById("passwordAdd");
        const confirmPasswordAdd = document.getElementById("confirmPasswordAdd");
        if (passwordAdd && confirmPasswordAdd) {
            const passwordAddError = document.getElementById("passwordAdd-error");
            const confirmPasswordAddError = document.getElementById("confirmPasswordAdd-error");

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

        // Password validation for Edit Mode
        const passwordEdit = document.getElementById("passwordEdit");
        const confirmPasswordEdit = document.getElementById("confirmPasswordEdit");
        if (passwordEdit && confirmPasswordEdit) {
            const passwordEditError = document.getElementById("passwordEdit-error");
            const confirmPasswordEditError = document.getElementById("confirmPasswordEdit-error");

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
    }

    window.validateUserForm = validateUserForm;
});

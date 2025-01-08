import {enableInput , selectValue , hiddenMethodInput ,disableInputs,hideInputs,disableSelects}
    from '/public/js/utils.js'

export const addUser = (user) => {
    $("#addUserBtn").on("click", (e) => {
        $(".error-msg").addClass("hidden");
        const btn = e.target;
        const nom = $("#nom");
        const prenom = $("#prenom");
        const email = $("#email");
        const password = $("#password");
        const cin = $("#cin");
        const tel = $("#telephone");
        const adresse = $("#adresse");
        const dateNaissance = $("#dateNaissance");
        const role = $("#role-select");
        const genre = $("#genre");
        $("#modalTitle").text("Ajouter un nouvel utilisateur");

        disableInputs(
            "bg-gray-100", "bg-gray-300",
            false, nom, prenom, email, tel,
            adresse, cin, dateNaissance,
            role, genre
        );

        selectValue(role, "");
        selectValue(genre, "");
        hideInputs(false, $("#confirmBtn"));

        $("#userModalForm").attr("action", "/users");
        hiddenMethodInput(true);

        $("#confPWD").removeClass("hidden").val("");
        nom.val("");
        prenom.val("");
        email.val("");
        password.val("");
        cin.val("");
        tel.val("");
        adresse.val("");
        dateNaissance.val("");
    })
}

export const viewUserInfo = () => {
    $(".viewBtns").each((i, btn) => {
        $(btn).on("click", (e) => {
            const btn = $(e.currentTarget);
            const nom = $("#nom");
            const prenom = $("#prenom");
            const email = $("#email");
            const cin = $("#cin");
            const tel = $("#telephone");
            const adresse = $("#adresse");
            const dateNaissance = $("#dateNaissance");
            const role = $("#role-select");
            const genre = $("#genre");

            const nomdata = btn.data("nom");
            const prenomdata = btn.data("prenom");
            const emaildata = btn.data("email");
            const teldata = btn.data("tel");
            const cinData = btn.data("cin");
            const addressData = btn.data("add");
            const dateData = btn.data("date");
            const roleData = btn.data("role");
            const genreData = btn.data("genre");


            $("#modalTitle").text(`Consulter les Informations de ${nomdata} ${prenomdata}`);

            disableInputs("bg-gray-300", "bg-gray-100", true, nom, prenom, email ,adresse,cin ,tel , dateNaissance);
            hideInputs(true, $("#confirmBtn"));

            nom.val(nomdata);
            prenom.val(prenomdata);
            email.val(emaildata);
            cin.val(cinData);
            tel.val(teldata);
            adresse.val(addressData);
            dateNaissance.val(dateData);
            selectValue(role, roleData);
            selectValue(genre, genreData);

            $("#userModalForm").attr("action", `/users/${btn.data("id")}`);
            hiddenMethodInput(false);
            disableSelects("bg-gray-300", "bg-gray-100", true, role, genre);

            $("#confPWD").addClass("hidden");
            $("#pwd").addClass("hidden");
        })
    })
}
export const modifyUser = () => {
    $(".modBtns").each((i, btn) => {
        $(btn).on("click", (e) => {
            console.log(e.currentTarget);
            const btn = $(e.currentTarget);
            const nom = $("#nom");
            $(".error-msg").addClass("hidden");
            const prenom = $("#prenom");
            const email = $("#email");
            const password = $("#pwd");
            const confPwd = $("#confPWD");
            const cin = $("#cin");
            const tel = $("#telephone");
            const adresse = $("#adresse");
            const role = $("#role-select");
            const dateNaissance = $("#dateNaissance");
            const genre = $("#genre");

            const nomdata = btn.data("nom");
            const prenomdata = btn.data("prenom")

            $("#modalTitle").text(`Modifier l'utilisateur ${nomdata} ${prenomdata}`);
            disableInputs(
                "bg-gray-100", "bg-gray-300",
                false, nom, prenom, email, tel,
                adresse, cin, dateNaissance,
                role, genre
            );
            nom.val(nomdata);
            prenom.val(prenomdata);
            email.val(btn.data("email"));
            cin.val(btn.data("cin"));
            adresse.val(btn.data("add"));
            tel.val(btn.data("tel"));
            dateNaissance.val(btn.data("date"));
            password.val("");
            confPwd.val("");
            selectValue(role, btn.data("role"));
            selectValue(genre, btn.data("genre"));

            $("#userModalForm").attr("action", `/users/${btn.data("id")}`);
            hiddenMethodInput(false);
            hideInputs(false, $("#confirmBtn"));

            $("#password").removeAttr("required");
            $("#confirmPassword").removeAttr("required");
            confPwd.removeClass("hidden");
            password.removeClass("hidden");
            telephone.addClass("hidden");
            cin.addClass("hidden");

        })
    })
}

export const deleteUser = () => {
    $(".deleteBtns").each((i, btn) => {
        $(btn).on("click", (e) => {
            const btn = $(e.currentTarget);
            const userId = $(btn).data("id");
            const nom = btn.data("nom");
            const prenom = btn.data("prenom");
            const email = btn.data("email");
            const cin = btn.data("cin");

            $("#deleteUserModal #nomComplet").text(`${nom} ${prenom}`);
            $("#deleteUserModal #email").text(email);
            $("#deleteUserModal #cin").text(cin);
            $("#deleteUserModalForm").attr("action", `/users/${userId}`);
        });
    });
};


import { enableInputs, selectValue } from '/public/js/utils.js';

export const deleteProf = () => {
    $(".deleteBtns").each((i, btn) => {
        console.log(btn);

        $(btn).on("click", (e) => {
            const btn = $(e.currentTarget);
            console.log(btn);
            const userId = $(btn).data("id");
            const nom = btn.data("nom");
            const prenom = btn.data("prenom");
            const email = btn.data("email");
            const cin = btn.data("cin");
            console.log(userId,nom,prenom,email,cin);

            $("#deleteProfModal #nomComplet").text(`${nom} ${prenom}`);
            $("#deleteProfModal #email").text(email);
            $("#deleteProfModal #cin").text(cin);
            $("#deleteProfModalForm").attr("action", `/professeur/${userId}`);
        });
    });
};
export const addProfessor = () => {
    $("#addProfBtn").on("click", () => {
        // Reset all form inputs for Add Mode
        const nom = $("#nom");
        const prenom = $("#prenom");
        const email = $("#email");
        const cin = $("#cin");
        const tel = $("#telephone");
        const adresse = $("#adresse");
        const dateNaissance = $("#dateNaissance");
        const grade = $("#grade");
        const genre = $("#genre");
        const dateDernierDiplome = $("#dateDernierDiplome");
        const dateEmbauche = $("#dateEmbauche");

        // Clear qualifications container
        $("#qualifications-container").empty();

        // Reset form
        enableInputs(nom, prenom, email, cin, tel, adresse, dateNaissance, dateDernierDiplome, dateEmbauche);
        selectValue(grade, "");
        selectValue(genre, "");
        $("#professorForm").attr("action", "/professeur");
        $("#pageTitle").text("Ajouter un Professeur");
        hiddenMethodInput(true);
    });
};
export const editProfessor = () => {
    $(".modBtns").each((i, btn) => {
        console.log(btn);
        $(btn).on("click", (e) => {
            const button = $(e.currentTarget);
console.log(button);
            // Fetch data from the button
            const profId = button.data("id");
            const nomdata = button.data("nom");
            const prenomdata = button.data("prenom");
            const emaildata = button.data("email");
            const teldata = button.data("tel");
            const cinData = button.data("cin");
            const addressData = button.data("add");
            const dateData = button.data("date");
            const gradeData = button.data("grade");
            const genreData = button.data("genre");
            const dernierDiplomeData = button.data("dernierdiplome");
            const embaucheData = button.data("embauche");
            const qualificationsData = JSON.parse(button.data("qualifications") || "[]");

            // Store data in sessionStorage
            sessionStorage.setItem("professorData", JSON.stringify({
                profId,
                nomdata,
                prenomdata,
                emaildata,
                teldata,
                cinData,
                addressData,
                dateData,
                gradeData,
                genreData,
                dernierDiplomeData,
                embaucheData,
                qualificationsData
            }));

            // Redirect to the edit form (same page or another page)
            window.location.href = `/professeur/viewprof/${profId}`;
        });
    });
};

// Dynamic Qualification Handling
document.addEventListener("DOMContentLoaded", function () {
    let qualificationIndex = 1;

    window.addQualification = function () {
        const container = document.getElementById('qualifications-container');
        const newQualification = `
            <div class="flex gap-4">
                <input type="text"
                       name="qualifications[${qualificationIndex}].intitule"
                       placeholder="Saisir Intitulé"
                       class="w-full py-3 p-3 rounded border"
                       required />
                <input type="date"
                       name="qualifications[${qualificationIndex}].dateObtention"
                       class="w-full p-3 rounded border"
                       required />
                <button type="button"
                        class="px-3 py-2 rounded"
                        onclick="removeQualification(this)">
                    <img src="/public/minus.png" class="w-10 h-8">
                </button>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', newQualification);
        qualificationIndex++;
    };

    window.removeQualification = function (button) {
        button.parentElement.remove();
    };
});

export const hiddenMethodInput = (remove) => {
    const input = $("#alternateMethod");
    console.log(input);

    if (remove) {
        if (input.length > 0)
            input.remove();

        return;
    }

    if (input.length > 0)
        return;

    const alternateMethodInput =
        "<input id=\"alternateMethod\" type=\"hidden\" name=\"_method\" value=\"PUT\"/>";
    $("#profForm").append(alternateMethodInput);
}

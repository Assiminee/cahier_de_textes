const dipYears = {
    "MA": 5,
    "CI": 5,
    "LC": 3,
    "CP": 2
};

export const resetFiliereModal = () => {
    hiddenMethodInput(true);
    const affectationBtn = $("#affectationBtn");

    $("#filModalH3").text("Ajouter une Nouvelle Filière");
    $("#filIntitule").val("");
    $("#filModalForm").attr("action", `/filieres`);
    $("#years").val("");
    $("#diplomaType").val("");
    $("#coordinateur").val("");
    $("#dateExpiration").val("");
    $("#dateReconnaissance").val("");

    if (!affectationBtn.hasClass("hidden"))
        affectationBtn.addClass("hidden");
}

const hiddenMethodInput = (remove) => {
    const input = $("#alternateMethod");

    if (remove) {
        if (input.length > 0)
            input.remove();

        return;
    }

    if (input.length > 0)
        return;

    const alternateMethodInput =
        "<input id=\"alternateMethod\" type=\"hidden\" name=\"_method\" value=\"PUT\"/>";

    $("#filModalForm").append(alternateMethodInput);
}

export const prefillFiliereForm = () => {
    const btns = $(".modalBtns");
    const affectationBtn = $("#affectationBtn");

    for (const btn of btns) {
        $(btn).on("click", () => {
            const id = $(btn).data("id");
            const intitule = $(btn).data("intitule");
            const dateExp = $(btn).data("dateexp");
            const dateRec = $(btn).data("daterec");
            const diplome = $(btn).data("diplome");
            const coordinateur = $(btn).data("coordinateur");
            const affectations = $(btn).data("affectations");

            hiddenMethodInput(false);
            $("#filModalH3").text(`Modifier la Filière ${intitule}`);
            $("#filIntitule").val(intitule);
            $("#filModalForm").attr("action", `/filieres/${id}`);
            $("#years").val(dipYears[`${diplome}`]);
            $(`#diplomaType option[value=${diplome}]`).attr("selected", "selected");
            $(`#coordinateur option[value='${coordinateur}']`).attr("selected", "selected");
            $("#dateExpiration").val(dateExp);
            $("#dateReconnaissance").val(dateRec);

            affectationBtn.data("affectations", affectations);
            affectationBtn.data("filId", id);
            affectationBtn.data("intitule", intitule);
            affectationBtn.data("years", dipYears[`${diplome}`]);

            if (affectationBtn.hasClass("hidden"))
                affectationBtn.removeClass("hidden");
        });
    }
}

export const updateYears = () => {
    $("#diplomaType").on("change", (e) => {
        const yearsInput = $("#years");
        const type = $(e.target).val();
        const years = dipYears[`${type}`];

        yearsInput.val(years);
    });
}

export const prefillAffectationsForm = () => {
    const btn = $("#affectationBtn");
    const niveauSelect = $("#niveau");
    const semesterSelect = $("#semestre");
    const years = $(btn).data("years");
    const aff = $(btn).data("affectations");
    const filId = $(btn).data("filId");
    const filTitle = $(btn).data("intitule");

    $("#affectationModalH3").text("Gestion des Affectations : " + filTitle);

    semesterSelect.val("-1");
    resetSelects();
    createOptions(niveauSelect, years);
    semesterSelect.on("change", (e) => {
        const semester = Number($(e.target).val());

        if (semester === -1) {
            resetSelects();
            return;
        }

        const niveau = Number(niveauSelect.val());
        const affectations = getAffectationList(aff, niveau, semester);

        if (affectations === undefined)
            return;

        for (let i = 0; i < affectations.length; i++) {
            const affectation = affectations[i];

            $(`#aff-${i}`).val(affectation.id);
            $(`#module-${i}`).val(affectation.module.id);
            $(`#prof-${i}`).val(affectation.professeur.id);
        }
    });

    $("#affectationModalForm").attr("action", `/filieres/${filId}/affectations`);
}

const createOptions = (select, years) => {
    select.find("option:not(:first)").remove();

    for (let i = 1; i <= years; i++) {
        const $option = $('<option>', {
            value: i,
            text: "Niveau " + i,
            id: "N-" + i
        });
        select.append($option);

        select.on("change", (e) => {
            const niveau = Number($(e.target).val());

            $("#SI")
                .text(niveau === -1 ? "" : "S-" + (niveau * 2 - 1))
                .attr("value", niveau === -1 ? "" : (niveau * 2 - 1));

            $("#SI2")
                .text(niveau === -1 ? "" : "S-" + niveau * 2)
                .attr("value", niveau === -1 ? "" : niveau * 2);

            $("#semestre").val("-1");

            resetSelects();
        });
    }
}

const resetSelects = () => {
    for (let i = 0; i < 8; i++) {
        $(`#aff-${i}`).val("");
        $(`#module-${i}`).val("");
        $(`#prof-${i}`).val("");
    }
}

const getAffectationList = (affObj, niveau, semester) => {
    if (!(`N-${niveau}` in affObj)) {
        resetSelects();
        return;
    }

    let affectations = affObj[`N-${niveau}`];

    if (!(`S-${semester}` in affectations)) {
        resetSelects();
        return;
    }

    return affectations[`S-${semester}`];
}



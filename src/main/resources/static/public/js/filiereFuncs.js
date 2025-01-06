export const dipYears = {
    "MA": 5,
    "CI": 5,
    "LC": 3,
    "CP": 2
};

const dipName = {
    "MA": "Master",
    "CI": "Cycle d'Ingenieur",
    "LC": "Licence",
    "CP": "Classes Préparatoires"
};

const setSelect = (select, value) => {
    $(`#${select} option`).removeAttr("selected");

    if (value)
        $(`#${select} option[value=${value}]`).attr("selected", "selected");
    else
        $(`#${select} option:first-child`).attr("selected", "selected");
}

export const resetFiliereModal = () => {
    hiddenMethodInput(true);

    const intitule = $("#filIntitule");
    const dateRec = $("#dateReconnaissance");
    const curProf = $("#curProf");

    if (!curProf.hasClass("hidden"))
        curProf.addClass("hidden");

    disableInputs("bg-gray-100", "bg-gray-300", false, intitule, dateRec);
    hideInputs(false, $("#diplomaType"), $("#coordinateur"), $("#confirmBtn"));
    hideInputs(true, $("#diplomeInp"), $("#coordinateurInp"));

    $("#filModalH3").text("Ajouter une Nouvelle Filière");
    $("#filModalForm").attr("action", `/filieres`);
    intitule.val("");
    $("#years").val("");
    setSelect("diplomaType");
    setSelect("coordinateur");
    dateRec.val("");
    $("#dateExpiration").val("");

    updateExpDate();
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

    for (const btn of btns) {
        $(btn).on("click", () => {
            hiddenMethodInput(false);

            const id = $(btn).data("id");
            const intitule = $("#filIntitule");
            const dateExp = $("#dateExpiration");
            const dateRec = $("#dateReconnaissance");
            const coordinateurId = $(btn).data("coordinateurid");
            const coordinateurName = $(btn).data("coordinateurname");
            const curProf = $("#curProf");
            const diplome = $(btn).data("diplome");

            if (curProf.hasClass("hidden"))
                curProf.removeClass("hidden");


            disableInputs("bg-gray-100", "bg-gray-300", false, intitule, dateExp, dateRec);
            hideInputs(false, $("#diplomaType"), $("#coordinateur"), $("#confirmBtn"));
            hideInputs(true, $("#diplomeInp"), $("#coordinateurInp"));

            $("#filModalH3").text(`Modifier la Filière ${$(btn).data("intitule")}`);
            intitule.val($(btn).data("intitule"));
            $("#filModalForm").attr("action", `/filieres/${id}`);
            $("#years").val(dipYears[`${diplome}`]);
            setSelect("diplomaType", diplome);
            curProf.val(coordinateurId).text(coordinateurName);
            setSelect("coordinateur", coordinateurId);
            dateRec.val($(btn).data("daterec"));
            dateExp.val($(btn).data("dateexp"));

            updateExpDate();
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

export const updateExpDate = () => {
    const recInput = $("#dateReconnaissance");
    const rec = Date.parse(recInput.val());
    const exp = $("#dateExpiration");

    if (!rec) {
        exp.attr("disabled", "disabled")
            .removeAttr("required")
            .addClass("bg-gray-300")
            .removeClass("bg-gray-100")
            .val("");

        recInput.removeAttr("required").removeAttr("name");

        return;
    }

    exp.attr("required", "required")
        .removeAttr("disabled")
        .addClass("bg-gray-100")
        .removeClass("bg-gray-300");

    recInput.attr("required", "required").attr("name", "dateReconnaissance");

    const recDate = new Date(rec);
    const minExpDate = new Date(recDate.getFullYear() + 5, recDate.getMonth(), recDate.getDate());
    exp.attr("min", minExpDate.toISOString().split('T')[0]);
}

export const setDeleteModalContent = () => {
    $(".filDelBtns").each((index, btn) => {
        $(btn).on("click", (e) => {
            const id = $(e.currentTarget).data("filid");
            const intitule = $(e.currentTarget).data("filint");

            $("#deleteModalP").html(`Êtes vous sûr de vouloir supprimer la filière <span id="deleteModalSpan" class="font-bold text-[#B10C74]">'${intitule}'</span>?`);
            $("#deleteModalForm").attr("action", `/filieres/${id}`);
        })
    });
}

export const viewFiliereInfo = () => {
    $(".viewBtns").each((index, btn) => {
        $(btn).on("click", (e) => {
            const btn = $(e.currentTarget);
            const intitule = $("#filIntitule");
            const dateExp = $("#dateExpiration");
            const dateRec = $("#dateReconnaissance");
            const nombreAnnees = $("#years");
            const diplome = $(btn).data("diplome");

            $("#filModalH3").text("Filière " + btn.data("intitule"));

            disableInputs("bg-gray-300", "bg-gray-100", true, intitule, dateExp, dateRec);
            hideInputs(true, $("#diplomaType"), $("#coordinateur"), $("#confirmBtn"));

            intitule.val(btn.data("intitule"));
            dateExp.val(btn.data("dateexp"));
            dateRec.val(btn.data("daterec"));
            nombreAnnees.val(dipYears[`${diplome}`]);

            $("#diplomeInp").removeClass("hidden").val(dipName[`${btn.data("diplome")}`]);
            $("#coordinateurInp").removeClass("hidden").val(btn.data("coordinateurname"));
        })
    })
}

const disableInputs = (addClass, removeClass, disable, ...inputs) => {
    for (const input of inputs) {
        if (disable)
            input.attr("disabled", "disabled");
        else
            input.removeAttr("disabled");

        if (!input.hasClass(addClass))
            input.addClass(addClass);

        if (input.hasClass(removeClass))
            input.removeClass(removeClass)
    }
}

const hideInputs = (hide, ...inputs) => {
    for (const input of inputs) {
        if (hide && !input.hasClass("hidden"))
            input.addClass("hidden");

        else if (!hide && input.hasClass("hidden"))
            input.removeClass("hidden");
    }
}
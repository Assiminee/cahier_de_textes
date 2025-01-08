export const addModule = () => {
    $("#addBtns").on("click", (e) => {
        const intitule = $("#moduleIntitule");
        const responsable = $("#responsable");
        const nombreHeures = $("#nombreHeures");
        const modeEvaluation = $("#modeEvaluation");

        $("#moduleModalTitle").text("Ajouter un nouveau module");
        enableInputs(intitule, nombreHeures);

        intitule.removeAttr("disabled");
        nombreHeures.removeAttr("disabled");

        selectValue(responsable, "");
        selectValue(modeEvaluation, "");
        hideInputs(false, $("#confirmBtn"));

        $("#moduleModalForm").attr("action", `/modules`);

        hiddenMethodInput(true);
    });
};

export const viewModuleInfo = () => {
    $(".viewBtns").each((i, btn) => {
        $(btn).on("click", (e) => {

            const btn = $(e.currentTarget);
            const intitule = $("#moduleIntitule");
            const responsable = $("#responsable");
            const nombreHeures = $("#nombreHeures");
            const modeEvaluation = $("#modeEvaluation");

            const intituleData = btn.data("intitule");
            const responsableData = btn.data("responsable");
            const heuresData = btn.data("nombreheures");
            const evaluationData = btn.data("modeevaluation");
            console.log(intituleData,responsableData,heuresData,evaluationData);
            $("#moduleModalTitle").text(`Consulter les Informations du Module: ${intituleData}`);
            disableInputs("bg-gray-300", "bg-gray-100", true, intitule, nombreHeures);
            hideInputs(true, $("#confirmBtn"));

            intitule.val(intituleData);
            nombreHeures.val(heuresData);
            selectValue(responsable, responsableData);
            selectValue(modeEvaluation, evaluationData);

            hiddenMethodInput(false);
            disableSelects("bg-gray-300", "bg-gray-100", true, responsable, modeEvaluation);
        });
    });
};

export const modifyModule = () => {
    $(".modBtns").each((i, btn) => {
        $(btn).on("click", (e) => {
            const button = $(e.currentTarget); // Correctly target the clicked button

            // Fetch data attributes
            const intituleData = button.data("intitule");
            const responsableData = button.data("responsable");
            const heuresData = button.data("nombreheures");
            const evaluationData = button.data("modeevaluation");
            // Debug: Log fetched data for verification
            console.log({
                intituleData,
                responsableData,
                heuresData,
                evaluationData
            });

            $("#moduleModalTitle").text(`Éditer le module ${intituleData}`);

            $("#moduleIntitule").removeAttr("disabled");
            $("#nombreHeures").removeAttr("disabled");

            enableInput($("#moduleIntitule"), intituleData);
            enableInput($("#nombreHeures"), heuresData);
            selectValue($("#responsable"), responsableData);
            selectValue($("#modeEvaluation"), evaluationData);
            hideInputs(false, $("#confirmBtn"));
            // Set form action
            $("#moduleModalForm").attr("action", `/modules/${button.data("id")}`);
            hiddenMethodInput(false);
        });
    });
};

export const deleteModule = () => {
    $(".deleteBtns").each((i, btn) => {
        $(btn).on("click", (e) => {
            const btn = $(e.currentTarget);
            const moduleId = btn.data("id");
            const intitule = btn.data("intitule");

            $("#deleteModuleModal #intitule").text(intitule);
            $("#deleteModuleModalForm").attr("action", `/modules/${moduleId}`);
        });
    });
};

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
const disableSelects = (addClass, removeClass, disable, ...selects) => {
    for (const select of selects) {
        if (disable)
            select.attr("disabled", "disabled");
        else
            select.removeAttr("disabled");

        if (!select.hasClass(addClass))
            select.addClass(addClass);

        if (select.hasClass(removeClass))
            select.removeClass(removeClass)
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

const enableInputs = (...inputs) => {
    for (const input of inputs)
        input.val("")
            .addClass("bg-gray-100")
            .removeClass("bg-gray-300")
            .prop("readonly", false);
}

const enableInput = (input, val) => {
    input.val(val)
        .addClass("bg-gray-100")
        .removeClass("bg-gray-300")
        .prop("readonly", false);
}

const selectValue = (select, val) => {
    select.val(val)
        .removeAttr("disabled")
        .addClass("bg-gray-100")
        .removeClass("bg-gray-300")
        .find("option")
        .removeAttr("selected");

    if (val === "")
        select.find("option:first-child").attr("selected", "selected");
    else
        select.find(`option[value="${val}"]`).attr("selected", "selected");
}

const hiddenMethodInput = (remove) => {
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
    $("#moduleModalForm").append(alternateMethodInput);
}

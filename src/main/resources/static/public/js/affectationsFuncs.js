export const fillAffectationForm = () => {
    niveauSelectOnChange();
    semesterSelectOnChange();
    affectationDivSelectsOnChange();
    addAffectationButtonOnClick();
}


export const interceptForm = () => {
    const filiereId = $("#affectationDiv").data("filid");

    $(".affForms").each((index, form) => {
        $(form).on("submit", async (e) => {
            e.preventDefault();
            e.stopPropagation();

            const form = $(e.target);
            const index = form.attr("id").slice(-1);
            const formData = new FormData(e.target);
            const url = $(e.target).attr("action");
            const method = $(e.target).attr("method");
            const data = {};
            formData.forEach((value, key) => {
                data[key] = value;
            });

            const params = {
                method: method, body: JSON.stringify(data),
                headers: {"Content-type": "application/json"}
            }

            const response = await fetch(url, params);
            handleResponse(response.status);

            form.attr("method", "PUT");

            const affectation = await response.json();

            form.attr("action", `/filieres/${filiereId}/affectations/${affectation.id}`);

            setAffectation(affectation);

            $(`#save-${index}`).addClass("hidden");
            $(`#modify-${index}`).removeClass("hidden");

        })
    });
}

/**
 * Defines the behavior of the niveau select
 */
const niveauSelectOnChange = () => {
    $("#niveau").on("change", (e) => {
        // Resets and hides all the forms
        resetAffectationForms();

        const val = $(e.target).val();
        const niveau = Number(val);

        // Sets the niveau select
        selectOption($(e.target), niveau);

        // Sets the semester select to the default option
        // (empty string for no selected option)
        selectOption($("#semestre"), "");

        // removes the hidden class from the semester options
        // and sets their values based on what the value of
        // niveau is
        setSemesterOptions(val, niveau);
    });
}

/**
 * Defines the behavior of the semester select
 */
const semesterSelectOnChange = () => {
    $("#semestre").on("change", (e) => {
        // Resets all the forms
        resetAffectationForms();

        const filiereId = $("#affectationDiv").data("filid");
        const niveau = $("#niveau").find("option[selected=selected]").val();
        const semester = $(e.target).val();

        // Gets the list of affectations associated with a "niveau" and a "semestre"
        const affs = getAffectations(`N-${niveau}`, `S-${semester}`);

        // Sets "semestre" and "niveau" in the hidden inputs of all the forms
        // Reason: these values are required to properly store the data in the
        // database
        $(".semestreInputs").val(semester);
        $(".niveauInputs").val(niveau);

        // Adds the "selected" attribute to the option whose value matches
        // the selected semester
        selectOption($(e.target), semester);

        // If no affectations exist for a niveau/semestre, an add button is
        // revealed to allow the user to add a new affectation
        if (affs.length === 0) {
            $("#addAffectation").removeClass("hidden");
            return;
        }

        let i = 1;

        // Loops over the affectation list and sets all the select
        // options necessary to match the data in the array
        for (const aff of affs) {
            if (i > 8)
                break;

            const currMod = $(`#module-${i}`);
            const currProf = $(`#prof-${i}`);
            const currDay = $(`#jour-${i}`);
            const currStart = $(`#start-${i}`);
            const form = $(`#affForm-${i}`);

            // Reveals the hidden form, sets the action and the method
            form.removeClass("hidden")
                .attr("action", `/filieres/${filiereId}/affectations/${aff.id}`)
                .attr("method", "PUT");

            // Removes the "selected" attribute from the default option
            // of all the selects in the form
            unselectDefaultOption(currMod, currProf, currDay, currStart);

            // Sets the values of the selects (module, professeur, jour, heureDebut)
            // and adds the attribute "selected" to the option whose value is the same
            // as the one in the current affectation instance
            setSelectedData(currMod, aff.module.id);
            setSelectedData(currProf, aff.professeur.id);
            setSelectedData(currDay, aff.jour);
            setSelectedData(currStart, aff.heureDebut);

            // Sets heureFin based on what the value of heureDebut is
            $(`#end-${i}`).attr("required", "required").val(aff.heureFin + " : 00");
            i++;
        }

        // Hides or reveals the add button depending on whether the maximum
        // number of visible forms (max 8) has been reached or not
        if (i < 8)
            $("#addAffectation").removeClass("hidden");
        else
            $("#addAffectation").addClass("hidden");
    })
}

/**
 * Retrieves the sessionStorage item containing the affectations
 * object and fetches the list of affectations associated with
 * a "niveau" and a "semestre"
 * @param niveau a String in the format N-{integer}
 * @param semester a String in the format S-{integer}
 * @returns {*|*[]} a list of affectations
 */
const getAffectations = (niveau, semester) => {
    const affectations = JSON.parse(sessionStorage.getItem("affectations"));

    if (!(niveau in affectations))
        return [];

    if (!(semester in affectations[`${niveau}`]))
        return [];

    return affectations[`${niveau}`][`${semester}`];
}

/**
 * Resets all the forms and hides them
 */
const resetAffectationForms = () => {
    const filiereId = $("#affectationDiv").data("filid");

    for (let i = 1; i < 9; i++) {
        const currMod = $(`#module-${i}`);
        const currProf = $(`#prof-${i}`);
        const currDay = $(`#jour-${i}`);
        const currStart = $(`#start-${i}`);
        const form = $(`#affForm-${i}`);

        // Hide the div containing the affectation inputs
        form.addClass("hidden").attr("action", `/filieres/${filiereId}/affectations`).attr("method", "POST");

        // Reset current affectation div dropdowns
        resetSelects(currMod, currProf, currDay, currStart);

        // set the value of each heureFin to an empty string
        $(`#end-${i}`).val("");
    }
}

/**
 * Resets all selects by setting their value to an empty string,
 * adding the "selected" attribute to the default option, and
 *
 * @param selects
 */
const resetSelects = (...selects) => {
    for (const select of selects) {
        select.find("option").removeAttr("selected");
        select.val("").find("option:first-child").attr("selected", "selected");
        select.removeAttr("required");
    }
}

const selectOption = (select, value) => {
    select.find("option").removeAttr("selected");

    if (value === "")
        select.find("option:first-child").attr("selected", "selected");
    else
        select.find(`option[value="${value}"]`).attr("selected", "selected");
}

const unselectDefaultOption = (...selects) => {
    for (const select of selects)
        select.find("option:first-child").removeAttr("selected");
}

const affectationDivSelectsOnChange = () => {
    for (let i = 1; i < 9; i++) {
        const selects = {
            module: $(`#module-${i}`),
            prof: $(`#prof-${i}`),
            day: $(`#jour-${i}`),
            start: $(`#start-${i}`)
        };

        for (const [key, select] of Object.entries(selects))
            select.on("change", (e) => {
                const value = $(e.target).val();

                $(e.target)
                    .find("option")
                    .removeAttr("selected")
                    .filter(`[value="${value}"]`)
                    .attr("selected", "selected");

                if (key === "start") {
                    if (value === "")
                        $(`#end-${i}`).val("");
                    else
                        $(`#end-${i}`).val((value === "8" ? 12 : 18) + " : 00");
                }
            });
    }
}

const addAffectationButtonOnClick = () => {
    $("#addAffectation").on("click", (e) => {
        const res = $(".saveButtons:not(.hidden)");

        if (res.length > 0) {
            $("#alertMessage").text("Veuillez sauvegarder vos données avant de créer une nouvelle affectation.");
            $("#dangerAlert").removeClass("transition-opacity duration-300 ease-out opacity-0 hidden");
            return;
        }

        for (let i = 0; i < 9; i++) {
            const affForm = $(`#affForm-${i}`);

            if (!affForm.hasClass("hidden"))
                continue;

            affForm.removeClass("hidden").attr("method", "POST");
            $(`#modify-${i}`).addClass("hidden");
            $(`#save-${i}`).removeClass("hidden");

            setRequired($(`#module-${i}`), $(`#prof-${i}`), $(`#jour-${i}`), $(`#start-${i}`));

            if (i === 8)
                $(e.target).addClass("hidden");

            break;
        }
    })
}

const handleResponse = (status) => {
    const alertMessage = $("#alertMessage");
    const dangerAlertDiv = $("#dangerAlert");

    if (status === 200) {
        $("#successMessage").text("L'affectation a été sauvegarder avec succès");
        $("#successAlert").removeClass("transition-opacity duration-300 ease-out opacity-0 hidden");

        return;
    }

    if (status === 404)
        alertMessage.text("La filière que vous cherchez n'existe pas");
    else
        alertMessage.text("Une erreur s'est produite lors de la création de l'affectation. Veuillez réessayer plus tard.");

    dangerAlertDiv.removeClass("transition-opacity duration-300 ease-out opacity-0 hidden");
}

const setAffectation = (affectation) => {
    let affectations = JSON.parse(sessionStorage.getItem("affectations")) || {};
    const niveau = "N-" + $("#niveau").val();
    const semestre = "S-" + $("#semestre").val();

    if (!(niveau in affectations))
        affectations[niveau] = {};

    if (!(semestre in affectations[niveau]))
        affectations[niveau][semestre] = [];

    affectations[niveau][semestre].push(affectation);

    sessionStorage.setItem("affectations", JSON.stringify(affectations));
}

const setSelectedData = (select, value) => {
    select
        .val(value)
        .attr("required", "required")
        .find(`option[value="${value}"]`)
        .attr("selected", "selected");
}

const setRequired = (...selects) => {
    for (const select of selects)
        select.attr("required", "required");
}

const setSemesterOptions = (val, niveau) => {
    const SI = $("#SI");
    const SI2 = $("#SI2");

    if (val === "") {
        SI.addClass("hidden").removeAttr("value").text("");
        SI2.addClass("hidden").removeAttr("value").text("");

        return;
    }

    SI.removeClass("hidden")
        .attr("value", (niveau * 2 - 1))
        .text("S" + (niveau * 2 - 1));

    SI2.removeClass("hidden")
        .attr("value", niveau * 2)
        .text("S" + niveau * 2);
}
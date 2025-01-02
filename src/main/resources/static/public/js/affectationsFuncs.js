export const fillAffectationForm = () => {
    niveauSelectOnChange();
    $("#semestre").on("change", () => loadData());
    affectationDivSelectsOnChange();
    addAffectationButtonOnClick();
    deleteButtonsOnClick();
    editButtonsOnClick();
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
            const success = await handleResponse(response, 200, "L'affectation a été sauvegardée avec succès");

            if (!success)
                return;

            const affectation = await response.json();

            if (method === "POST")
                setAffectation(affectation);
            else if (method === "PUT")
                modifyAffectation(affectation);

            form.attr("method", "PUT");
            form.attr("action", `/filieres/${filiereId}/affectations/${affectation.id}`);

            $(`#save-${index}`).addClass("hidden").find("img").addClass("hidden");
            $(`#modify-${index}`).removeClass("hidden").find("img").removeClass("hidden");
            setDisabled(index);
        })
    });
}

export const interceptDeleteForm = () => {
    $("#deleteAffectationModalForm").on("submit", async (e) => {
        e.preventDefault();
        e.stopPropagation();

        const form = $(e.target);
        const action = form.attr("action");
        const affId = action.slice(action.lastIndexOf("/") + 1);

        const response = await fetch(action, {method: "DELETE"});

        const success = await handleResponse(response, 204, "L'affectation a été suprimée avec succès.");

        if (!success)
            return;

        const removed = removeAffectation(affId);

        if (!removed)
            console.log("Couldn't remove affectation from SessionStorage item");

        loadData();
    })
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
        resetAffectationForm(i, filiereId);
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
    $("#addAffectation:button").on("click", (e) => {
        if (forbidAddition())
            return;

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

const forbidAddition = () => {
    const res = $(".saveButtons:not(.hidden)");

    if (res.length === 0) return false;

    $("#alertMessage").text("Veuillez sauvegarder vos données avant de modifier ou créer une nouvelle affectation.");
    $("#dangerAlert").removeClass("transition-opacity duration-300 ease-out opacity-0 hidden");
    return true;
}

const handleResponse = async (response, code, msg) => {
    const alertMessage = $("#alertMessage");
    const dangerAlertDiv = $("#dangerAlert");
    const cls = "transition-opacity duration-300 ease-out opacity-0 hidden";

    if (response.status === code) {
        $("#successMessage").text(msg);
        $("#successAlert").removeClass(cls);

        return true;
    }

    const err = await response.text();

    alertMessage.text(err);
    dangerAlertDiv.removeClass(cls);

    return false;
}

const modifyAffectation = (affectation) => {
    let affectations = JSON.parse(sessionStorage.getItem("affectations")) || {};
    const niveau = "N-" + $("#niveau").val();
    const semestre = "S-" + $("#semestre").val();

    if (!(niveau in affectations))
        return false;

    if (!(semestre in affectations[niveau]))
        return false;

    affectations[niveau][semestre] = affectations[niveau][semestre].filter(
        (aff) => aff.id !== affectation.id
    );

    affectations[niveau][semestre].push(affectation);

    sessionStorage.setItem("affectations", JSON.stringify(affectations));
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
        select.attr("required", "required")
            .removeAttr("disabled")
            .removeClass("bg-gray-300")
            .addClass("bg-gray-100");
}

const setDisabled = (index) => {
    const selects = [
        $(`#module-${index}`), $(`#prof-${index}`),
        $(`#start-${index}`), $(`#jour-${index}`)
    ];

    for (const select of selects)
        select.prop("disabled", true)
            .removeClass("bg-gray-100")
            .addClass("bg-gray-300");
}

const enableSelects = (index) => {
    const selects = [
        $(`#module-${index}`), $(`#prof-${index}`),
        $(`#start-${index}`), $(`#jour-${index}`)
    ];

    for (const select of selects)
        select.prop("disabled", false)
            .removeClass("bg-gray-300")
            .addClass("bg-gray-100");
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

const deleteButtonsOnClick = () => {
    const filiereId = $("#affectationDiv").data("filid");

    $(".removeBtns").each((index, btn) => {
        $(btn).on("click", (e) => {
            const deleteModal = document.getElementById("deleteAffectationModal");
            const modal = new Modal(deleteModal);
            const index = $(e.currentTarget).attr("id").slice(-1);
            const form = $(`#affForm-${index}`);
            const delAffForm = $("#deleteAffectationModalForm");

            if (form.attr("method") === "POST") {
                resetAffectationForm(index, filiereId);
                $("#addAffectation:button").removeClass("hidden");
                $("#addAffectation img").removeClass("hidden");
                return;
            }

            modal.show();
            $(".dismissModal").on("click", () => modal.hide());

            delAffForm.attr("action", form.attr("action"));
            delAffForm.data("index", index);

            $("#moduleTD").text(getSelectedOptionText("module", index));
            $("#profTH").text(getSelectedOptionText("prof", index));
            $("#jourTD").text(getSelectedOptionText("jour", index));
            $("#hoursTD").text(getSelectedOptionText("start", index) + " - " + $(`#end-${index}`).val());
        })
    })
}

const getSelectedOptionText = (element, index) => {
    return $(`#${element}-${index}`).find("option[selected=selected]").text()
}

const resetAffectationForm = (index, filiereId) => {
    const currMod = $(`#module-${index}`);
    const currProf = $(`#prof-${index}`);
    const currDay = $(`#jour-${index}`);
    const currStart = $(`#start-${index}`);
    const form = $(`#affForm-${index}`);

    // Hide the div containing the affectation inputs
    form.addClass("hidden").attr("action", `/filieres/${filiereId}/affectations`).attr("method", "POST");

    // Reset current affectation div dropdowns
    resetSelects(currMod, currProf, currDay, currStart);
    setDisabled(index);

    // Hide all the submit buttons
    $(".saveButtons").addClass("hidden").find("img").addClass("hidden");
    $(".modifyBtns").removeClass("hidden").find("img").removeClass("hidden");

    // set the value of each heureFin to an empty string
    $(`#end-${index}`).val("");
}

const removeAffectation = (affId) => {
    let affectations = JSON.parse(sessionStorage.getItem("affectations")) || {};

    if (Object.keys(affectations).length === 0) return false;

    const niveau = "N-" + $("#niveau").val();
    const semestre = "S-" + $("#semestre").val();

    if (!(niveau in affectations)) return false;

    if (!(semestre in affectations[`${niveau}`])) return false;

    let curAffList = affectations[niveau][semestre];
    const newList = curAffList.filter(item => item.id !== affId);

    if (curAffList.length === newList.length) return false;

    affectations[`${niveau}`][`${semestre}`] = newList;

    sessionStorage.setItem("affectations", JSON.stringify(affectations));

    return true;
}

const loadData = () => {
    // Resets all the forms
    resetAffectationForms();

    const filiereId = $("#affectationDiv").data("filid");
    const niveau = $("#niveau").find("option[selected=selected]").val();
    const semester = $("#semestre").val();

    // Gets the list of affectations associated with a "niveau" and a "semestre"
    const affs = getAffectations(`N-${niveau}`, `S-${semester}`);

    // Sets "semestre" and "niveau" in the hidden inputs of all the forms
    // Reason: these values are required to properly store the data in the
    // database
    $(".semestreInputs").val(semester);
    $(".niveauInputs").val(niveau);

    // Adds the "selected" attribute to the option whose value matches
    // the selected semester
    selectOption($("#semestre"), semester);

    // If no affectations exist for a niveau/semestre, an add button is
    // revealed to allow the user to add a new affectation
    if (affs.length === 0) {
        $("#addAffectation:button").removeClass("hidden");
        $("#addAffectation img").removeClass("hidden");
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
    if (i > 8)
        $("#addAffectation").addClass("hidden").find("img").addClass("hidden");
    else
        $("#addAffectation").removeClass("hidden").find("img").removeClass("hidden");
}

const editButtonsOnClick = () => {
    $(".modifyBtns").each((index, btn) => {
        $(btn).on("click", (e) => {
            if (forbidAddition())
                return;

            $(e.target).addClass("hidden").find("img").addClass("hidden");
            $(`#save-${index + 1}`).removeClass("hidden").find("img").removeClass("hidden");

            enableSelects(index + 1);
        })
    })
}
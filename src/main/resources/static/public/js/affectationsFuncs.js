export const fillAffectationForm = () => {
    const affDiv = $("#affectationDiv");
    const affectations = affDiv.data("affectations");
    niveauSelectOnChange();
    semesterSelectOnChange(affectations);
    affectationDivSelectsOnChange();
    console.log(affectations);
}

const niveauSelectOnChange = () => {
    const niveauSelect = $("#niveau");

    niveauSelect.on("change", (e) => {
        resetAffectationDivs();

        const val = $(e.target).val();
        const niveau = Number(val);
        const SI = $("#SI");
        const SI2 = $("#SI2");

        niveauSelect.find("option").removeAttr("selected");

        if (val === "")
            niveauSelect.find("option:first-child").attr("selected", "selected");
        else
            niveauSelect.find(`option[value=${niveau}]`).attr("selected", "selected");

        $("#semestre").val("");

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
            .text(niveau === -1 ? "" : "S" + niveau * 2);
    });
}

const semesterSelectOnChange = (affectations) => {
    $("#semestre").on("change", (e) => {
        resetAffectationDivs();

        const niveau = $("#niveau").find("option[selected=selected]").val();
        const semester = $(e.target).val();
        const affs = getAffectations(affectations, `N-${niveau}`, `S-${semester}`);

        if (affs.length === 0)
            return;

        let i = 1;

        for (const aff of affs) {
            const currMod = $(`#module-${i}`);
            const currProf = $(`#prof-${i}`);
            const currDay = $(`#jour-${i}`);
            const currStart = $(`#start-${i}`);

            $(`#affDiv-${i}`).removeClass("hidden");
            $(`#aff-${i}`).val(aff.id);

            unselectDefaultOption(currMod, currProf, currDay, currStart);

            currMod.attr("required", "required").find(`option[value=${aff.module.id}]`).attr("selected", "selected");
            currProf.attr("required", "required").find(`option[value=${aff.professeur.id}]`).attr("selected", "selected");
            currDay.attr("required", "required").find(`option[value=${aff.jour}`).attr("selected", "selected");
            currStart.attr("required", "required").find(`option[value='${aff.heureDebut}'`).attr("selected", "selected");

            $(`#end-${i}`).attr("required", "required").val(aff.heureFin + " : 00");
            i++;
        }

        if (i < 8)
            $("#addAffectation").removeClass("hidden");
        else
            $("#addAffectation").addClass("hidden");

    })
}

const getAffectations = (affectations, niveau, semester) => {
    if (!(niveau in affectations))
        return [];

    if (!(semester in affectations[`${niveau}`]))
        return [];

    return affectations[`${niveau}`][`${semester}`];
}

const resetAffectationDivs = () => {
    for (let i = 1; i < 9; i++) {
        let currMod = $(`#module-${i}`);
        let currProf = $(`#prof-${i}`);
        let currDay = $(`#jour-${i}`);
        let currStart = $(`#start-${i}`);

        // Hide the div containing the affectation inputs
        $(`#affDiv-${i}`).addClass("hidden");

        // Set the value of the hidden input to an empty string
        $(`#aff-${i}`).removeAttr("value");

        // Reset current affectation div dropdowns
        resetSelects(currMod, currProf, currDay, currStart);

        $(`#end-${i}`).val("");
    }
}

const resetSelects = (...selects) => {
    for (const select of selects) {
        select.find("option").removeAttr("selected");
        select.val("").find("option:first-child").attr("selected", "selected");
        select.removeAttr("required");
    }
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

export const interceptForm = () => {
    $("#affectationForm").on("submit", (e) => {
        e.preventDefault();
        e.stopPropagation();

        const formData = new FormData(e.target);

        for (const [key, val] of formData.entries()) {
            console.log(`${key} : ${val}`);
        }
    })
}
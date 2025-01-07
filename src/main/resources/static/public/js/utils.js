export const disableInputs = (addClass, removeClass, disable, ...inputs) => {
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

export const disableSelects = (addClass, removeClass, disable, ...selects) => {
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

export const hideInputs = (hide, ...inputs) => {
    for (const input of inputs) {
        if (hide && !input.hasClass("hidden"))
            input.addClass("hidden");

        else if (!hide && input.hasClass("hidden"))
            input.removeClass("hidden");
    }
}

export const enableInputs = (...inputs) => {
    for (const input of inputs)
        input.val("")
            .addClass("bg-gray-100")
            .removeClass("bg-gray-300")
            .prop("readonly", false);
}

export const enableInput = (input, val) => {
    input.val(val)
        .addClass("bg-gray-100")
        .removeClass("bg-gray-300")
        .prop("readonly", false);
}

export const selectValue = (select, val) => {
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
    $("#userModalForm").append(alternateMethodInput);
}
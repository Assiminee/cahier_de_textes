export const enableInputs = (bool) => {
    const add = "bg-gray-" + (bool ? "300" : "100");
    const remove = "bg-gray-" + (bool ? "100" : "300");

    $(".inputList").addClass(add).removeClass(remove).prop("disabled", bool);
    $(".toToggle").toggleClass("hidden");
}

export const formButtonsBehavior = () => {
    $("#modBtn").on("click", () => enableInputs(false));
    $("#cancelBtn").on("click", () => enableInputs(true));
}
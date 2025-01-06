export const checkboxOnChange = (e, index) => {
    const isChecked = e.target.checked;

    if (isChecked) {
        $(e.target).attr("name", `objectifs[${index}].atteint`);
        $(`#atteint-${index}-hidden`).removeAttr("name");
    } else {
        $(e.target).removeAttr("name");
        $(`#atteint-${index}-hidden`).attr("name", `objectifs[${index}].atteint`);
    }
}

export const erase = (index) => {
    $(`#objectifs-${index}-contenue`).val("");
    $(`#objectifs-${index}-atteint`).prop("checked", false).trigger('change');
}
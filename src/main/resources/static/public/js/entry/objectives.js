export const addObjective = () => {
    $(".addBtns").each((index, btn) => {
        $(btn).on("click", () => {
            addBtnOnClick(index);
        })
    })
}

export const addBtnOnClick = (index) => {
    $(`#add-${index}`).addClass("hidden").find("img").addClass("hidden");

    const objective = getObjective(index + 1);
    $(".objectives").append(objective);
}

export const removeBtnOnClick = (index) => {
    $(`#objectifDiv-${index}`).remove();
    $(`#add-${index - 1}`).removeClass("hidden").find("img").removeClass("hidden");
}

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

const getObjective = (index) => {
    return `
        <div class="flex gap-5 pt-5" id="objectifDiv-${index}">
            <button class="bg-transparent self-end mb-2 addBtns"
                    type="button" id="add-${index}" onclick="addBtnOnClick(${index})">
                <img src="/public/add.png" alt="add" class="w-8 h-8">
            </button>
            <button class="bg-transparent self-end mb-2 removeBtns"
                    type="button" id="remove-${index}" onclick="removeBtnOnClick(${index})">
                <img src="/public/minus.png" alt="delete" class="w-8 h-8">
            </button>
            <div class="flex-1 flex gap-5 justify-between">
                <div class="flex-1">
                    <label for="objectifs-${index}-contenue" class="block font-semibold text-gray-700">Objectifs</label>
                    <input type="text" name="objectifs[${index}].contenue" id="objectifs-${index}-contenue" required placeholder="Saisir un objectif"
                           class="w-full mt-1 p-3 border rounded-md">
                </div>
        
                <!-- Atteint -->
                <div class="flex items-center me-4 mt-6">
                    <input id="objectifs-${index}-atteint" type="checkbox" value="true" onchange="checkboxOnChange(event, ${index})"
                           class="w-7 h-7 text-2xl text-[#B10C74] bg-gray-700 border-gray-600 rounded focus:ring-[#B10C74] focus:ring-2 atteintCheckbox">
                    <label for="objectifs-${index}-atteint"
                           class="ms-2 text-2xl font-medium text-[#313863]">Atteint</label>
        
                    <input type="hidden" id="atteint-${index}-hidden" name="objectifs[${index}].atteint" value="false">
                </div>
            </div>
        </div>
    `
}
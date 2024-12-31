export const setMaxDate = () => {
    const today = new Date();
    const eighteenYearsAgo = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
    const formattedDate = eighteenYearsAgo.toISOString().split('T')[0];

    $("#dateNaissance").attr("max", formattedDate);
};

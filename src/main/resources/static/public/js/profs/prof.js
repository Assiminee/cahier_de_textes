export const deleteProf = () => {
    $(".deleteBtns").each((i, btn) => {
        console.log(btn);

        $(btn).on("click", (e) => {
            const btn = $(e.currentTarget);
            console.log(btn);
            const userId = $(btn).data("id");
            const nom = btn.data("nom");
            const prenom = btn.data("prenom");
            const email = btn.data("email");
            const cin = btn.data("cin");
            console.log(userId,nom,prenom,email,cin);

            $("#deleteProfModal #nomComplet").text(`${nom} ${prenom}`);
            $("#deleteProfModal #email").text(email);
            $("#deleteProfModal #cin").text(cin);
            $("#deleteProfModalForm").attr("action", `/professeurs/${userId}`);
        });
    });
};
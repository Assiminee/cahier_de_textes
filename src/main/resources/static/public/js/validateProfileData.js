import {enableInputs} from "./enableProfileFormInputs.js";

const validateProfileData = (role) => {
    $("#profileForm").on('submit', async (e) => {
        e.preventDefault();
        e.stopPropagation();

        const form = new FormData(e.target);
        const action = $(e.target).attr("action");

        try {
            const resp = await fetch(`/api/validate/email`, {
                method: "POST",
                headers: {'Content-Type': 'application/json',},
                body: JSON.stringify({email: form.get("email")})
            });

            if (resp.status === 200) {
                $("#email").removeClass("peer-invalid");
                $(e.target).submit();
                return;
            }

            const json = await resp.json();
            if (!("errors" in json))
                return;

            console.log("After error check");

            const errors = json.errors[0];

            if (!("arguments" in errors))
                return;

            console.log("After arguments check");

            const args = errors.arguments;
            console.log(args[0]);

            if (!("defaultMessage" in args[0]))
                return;

            console.log("After default message check");

            const messages = {
                duplicated: "Cet email existe déjà",
                blank: "Email requis",
                invalid: "Email invalid"
            }

            console.log(args[0]);
            const error = args[0].defaultMessage;
            const message = messages[`${error}`];
            console.log(message)

            // $("#email").prop("invalid", true);
            $("#email_p_msg").val(message).addClass("visible").removeClass("invisible");
            setTimeout(
                () => {
                    $("#email_p_msg").addClass("invisible").removeClass("visible");
                }, 10000
            )
            return;
        } catch (e) {
            console.error(e);
        }

        $("#cancelBtn").click();
    })
}



export default validateProfileData;
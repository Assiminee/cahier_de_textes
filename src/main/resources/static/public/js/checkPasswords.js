export const checkPasswords = (newPassword, confirmPassword) => {

    const pwdErr = $("#confPWDerr");

    if (newPassword.val() !== confirmPassword.val())
        pwdErr.addClass("hidden");
    else
        pwdErr.removeClass("hidden");
}
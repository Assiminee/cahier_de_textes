export const checkPasswords = (newPassword, confirmPassword) => {
    const form = $("#editPwdForm");

    form.on('submit', async (e) => {
        e.preventDefault();
        e.stopPropagation();

        const pwdErr = $("#confPWDerr");

        if (newPassword.val() !== confirmPassword.val()) {
            pwdErr.removeClass("hidden");
            return;
        }

        pwdErr.addClass("hidden");
        e.target.submit();
    });
}

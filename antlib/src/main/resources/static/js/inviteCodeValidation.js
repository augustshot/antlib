function initJoinFormValidation() {
    const joinForm = document.getElementById('joinForm');
    const inviteCodeInput = document.getElementById('inviteCode');
    const submitBtn = document.getElementById('invite-button');

    if (!joinForm || !inviteCodeInput) return;

    let errorDiv = document.getElementById('inviteCode-error');
    if (!errorDiv) {
        errorDiv = document.createElement('div');
        errorDiv.id = 'inviteCode-error';
        errorDiv.className = 'text-danger small mt-1 d-none';
        inviteCodeInput.parentNode.appendChild(errorDiv);
    }

    function validateInviteCode() {
        const code = inviteCodeInput.value.trim();

        if (!code) {
            setInvalid(inviteCodeInput, errorDiv, 'Введите код приглашения');
            return false;
        }

        if (code.length !== 10) {
            setInvalid(inviteCodeInput, errorDiv, 'Код должен содержать 10 символов');
            return false;
        }

        const validPattern = /^[A-Z0-9]+$/i;
        if (!validPattern.test(code)) {
            setInvalid(inviteCodeInput, errorDiv, 'Код может содержать только латинские буквы и цифры');
            return false;
        }

        setValid(inviteCodeInput, errorDiv);
        return true;
    }

    function setInvalid(input, errorElement, message) {
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        errorElement.classList.remove('d-none');
        errorElement.innerHTML = `${message}`;
        if (submitBtn) submitBtn.disabled = true;
    }

    function setValid(input, errorElement) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        errorElement.classList.add('d-none');
        errorElement.innerHTML = '';
        if (submitBtn) submitBtn.disabled = false;
    }

    inviteCodeInput.addEventListener('input', function() {
        this.value = this.value.toUpperCase();
        validateInviteCode();
    });

    inviteCodeInput.addEventListener('blur', validateInviteCode);



    submitBtn.addEventListener('click', function(e) {
            const isValid = validateInviteCode();
            if (!isValid) {
                e.preventDefault();
                inviteCodeInput.focus();
            }
            joinForm.submit();
        });
}

document.addEventListener('DOMContentLoaded', function() {
    initJoinFormValidation();
});
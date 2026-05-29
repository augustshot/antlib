window.addEventListener("load", init);

function init() {
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const newPasswordInput = document.getElementById('new-password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const passwordHiddenInput = document.getElementById('password');
    const form = document.getElementById('profile-form');

    const usernameError = document.getElementById('username-error');
    const emailError = document.getElementById('email-error');
    const newPasswordError = document.getElementById('new-password-error');
    const confirmPasswordError = document.getElementById('confirm-password-error');

    let usernameCheckTimer;
    let isUsernameValid = true;
    let originalUsername = usernameInput ? usernameInput.value : '';

    function setValid(input, isValid, errorElement, message) {
        if (!input) return;

        if (isValid) {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
            if (errorElement) {
                errorElement.classList.add('d-none');
                errorElement.innerHTML = '';
            }
        } else {
            input.classList.remove('is-valid');
            input.classList.add('is-invalid');
            if (errorElement && message) {
                errorElement.classList.remove('d-none');
                errorElement.innerHTML = message;
            }
        }
    }

    function resetValidation(input, errorElement) {
        if (!input) return;
        input.classList.remove('is-valid', 'is-invalid');
        if (errorElement) {
            errorElement.classList.add('d-none');
            errorElement.innerHTML = '';
        }
    }

    if (usernameInput) {
        usernameInput.addEventListener('input', function() {
            const username = usernameInput.value.trim();

            if (!username) {
                resetValidation(usernameInput, usernameError);
                isUsernameValid = false;
                return;
            }

            if (username === originalUsername) {
                setValid(usernameInput, true, usernameError, '');
                isUsernameValid = true;
                return;
            }

            clearTimeout(usernameCheckTimer);
            usernameCheckTimer = setTimeout(function() {
                fetch('/checkUsername?username=' + encodeURIComponent(username))
                    .then(response => response.json())
                    .then(data => {
                        if (data.exists) {
                            setValid(usernameInput, false, usernameError, 'Имя пользователя уже занято');
                            isUsernameValid = false;
                        } else {
                            setValid(usernameInput, true, usernameError, '');
                            isUsernameValid = true;
                        }
                    })
                    .catch(error => {
                        console.error('Ошибка:', error);
                    });
            }, 500);
        });
    }

    function validateEmail() {
        if (!emailInput) return true;

        const email = emailInput.value.trim();

        if (!email) {
            setValid(emailInput, false, emailError, 'Введите email');
            return false;
        }

        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            setValid(emailInput, false, emailError, 'Введите корректный email');
            return false;
        }

        setValid(emailInput, true, emailError, '');
        return true;
    }

    if (emailInput) {
        emailInput.addEventListener('input', validateEmail);
    }

    function validatePasswords() {
        let isValid = true;
        const newPassword = newPasswordInput ? newPasswordInput.value : '';
        const confirmPassword = confirmPasswordInput ? confirmPasswordInput.value : '';

        if (!newPassword && !confirmPassword) {
            resetValidation(newPasswordInput, newPasswordError);
            resetValidation(confirmPasswordInput, confirmPasswordError);
            return true;
        }

        if (!newPassword) {
            setValid(newPasswordInput, false, newPasswordError, 'Введите новый пароль');
            isValid = false;
        } else if (newPassword.length < 8) {
            setValid(newPasswordInput, false, newPasswordError, 'Пароль должен содержать минимум 8 символов');
            isValid = false;
        } else {
            setValid(newPasswordInput, true, newPasswordError, '');
        }

        if (!confirmPassword) {
            setValid(confirmPasswordInput, false, confirmPasswordError, 'Подтвердите новый пароль');
            isValid = false;
        } else if (newPassword !== confirmPassword) {
            setValid(confirmPasswordInput, false, confirmPasswordError, 'Пароли не совпадают');
            isValid = false;
        } else if (newPassword && newPassword === confirmPassword && newPassword.length >= 8) {
            setValid(confirmPasswordInput, true, confirmPasswordError, '');
        }

        return isValid;
    }

    if (newPasswordInput) {
        newPasswordInput.addEventListener('input', validatePasswords);
    }
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', validatePasswords);
    }

    function submitForm() {
        let isValid = true;

        if (usernameInput && (!usernameInput.value.trim() || !isUsernameValid)) {
            if (!usernameInput.value.trim()) {
                setValid(usernameInput, false, usernameError, 'Введите имя пользователя');
            }
            isValid = false;
        }

        if (!validateEmail()) isValid = false;
        if (!validatePasswords()) isValid = false;

        if (isValid) {
            if (passwordHiddenInput && newPasswordInput) {
                const newPassword = newPasswordInput.value;
                if (newPassword && newPassword.trim()) {
                    passwordHiddenInput.value = newPassword;
                }
                else{
                    passwordHiddenInput.value = "";
                }
            }
            form.submit();
        }
    }

    const submitButton = document.getElementById('submit-button');
    if (submitButton) {
        submitButton.addEventListener('click', function(e) {
            console.log("submit");
            e.preventDefault();
            submitForm();
        });
    }

    if (form) {
        form.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                submitForm();
            }
        });
    }

    const deleteProfileBtn = document.getElementById('deleteProfileBtn');
    if (deleteProfileBtn) {
        deleteProfileBtn.addEventListener('click', function() {
            const deleteModal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
            deleteModal.show();
        });
    }
}
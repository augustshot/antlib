window.addEventListener("load", init);

function init() {
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('reg-pass');
    const confirmInput = document.getElementById('confirm-pass');
    const form = document.getElementById('register-form');    const usernameError = document.getElementById('username-error');
    const emailError = document.getElementById('email-error');
    const passwordError = document.getElementById('password-error');
    const confirmError = document.getElementById('confirm-error');

    let usernameCheckTimer;
    let isUsernameValid = false;

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

    function checkPasswords() {
        if (!passwordInput || !confirmInput) return false;

        const password = passwordInput.value;
        const confirm = confirmInput.value;

        if (!password.trim() || !confirm.trim()) {
            resetValidation(confirmInput, confirmError);
            return false;
        }

        if (password === confirm) {
            setValid(confirmInput, true, confirmError, '');
            return true;
        } else {
            setValid(confirmInput, false, confirmError, 'Пароли не совпадают');
            return false;
        }
    }

    // Динамическая проверка username
    if (usernameInput) {
        usernameInput.addEventListener('input', function() {
            const username = usernameInput.value.trim();

            if (!username) {
                resetValidation(usernameInput, usernameError);
                isUsernameValid = false;
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

    function validateField(input, errorElement, fieldName) {
        if (!input) return false;

        const value = input.value.trim();

        if (!value) {
            setValid(input, false, errorElement, 'Введите ' + fieldName);
            return false;
        }

        if (fieldName === 'email') {
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(value)) {
                setValid(input, false, errorElement, 'Введите корректный email');
                return false;
            }
        }

        setValid(input, true, errorElement, '');
        return true;
    }

    function submitForm() {
        let isValid = true;

        // Проверка username
        if (!validateField(usernameInput, usernameError, 'имя пользователя') || !isUsernameValid) {
            isValid = false;
        }

        // Проверка email
        if (!validateField(emailInput, emailError, 'email')) {
            isValid = false;
        }

        // Проверка пароля
        if (!passwordInput || !passwordInput.value.trim()) {
            setValid(passwordInput, false, passwordError, 'Введите пароль');
            isValid = false;
        } else {
            setValid(passwordInput, true, passwordError, '');
        }

        // Проверка подтверждения пароля
        if (!confirmInput || !confirmInput.value.trim()) {
            setValid(confirmInput, false, confirmError, 'Подтвердите пароль');
            isValid = false;
        } else if (passwordInput && confirmInput && passwordInput.value !== confirmInput.value) {
            setValid(confirmInput, false, confirmError, 'Пароли не совпадают');
            isValid = false;
        } else {
            setValid(confirmInput, true, confirmError, '');
        }

        if (isValid) {
            form.submit();
        }

        return isValid;
    }

    // Обработчики событий
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            validateField(emailInput, emailError, 'email');
        });
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            if (!passwordInput.value.trim()) {
                setValid(passwordInput, false, passwordError, 'Введите пароль');
            } else {
                setValid(passwordInput, true, passwordError, '');
            }
            if (confirmInput.value.trim()) {
                checkPasswords();
            }
        });
    }

    if (confirmInput) {
        confirmInput.addEventListener('input', function() {
            if (!confirmInput.value.trim()) {
                setValid(confirmInput, false, confirmError, 'Подтвердите пароль');
                return;
            }
            checkPasswords();
        });

        confirmInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                submitForm();
            }
        });
    }

    const submitButton = document.getElementById('submit-button');
    if (submitButton) {
        submitButton.addEventListener('click', function(e) {
            e.preventDefault();
            submitForm();
        });
    }
}
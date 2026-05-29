window.addEventListener("load", init);

function init() {
    const usernameInput = document.getElementById('login');
    const passwordInput = document.getElementById('pass');
    const form = document.getElementById('login-form');
    const usernameError = document.getElementById('username-error');
    const passwordError = document.getElementById('password-error');
    const loginError = document.getElementById('errors_login');

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

    function validateField(input, errorElement, fieldName) {
        if (!input) return false;

        const value = input.value.trim();

        if (!value) {
            setValid(input, false, errorElement, 'Введите ' + fieldName);
            return false;
        }

        setValid(input, true, errorElement, '');
        return true;
    }

    function submitForm() {
        let isValid = true;

        if (loginError) {
            loginError.classList.add('d-none');
        }

        if (!validateField(usernameInput, usernameError, 'имя пользователя')) {
            isValid = false;
        }

        if (!validateField(passwordInput, passwordError, 'пароль')) {
            isValid = false;
        }

        if (isValid) {
            form.submit();
        }

        return isValid;
    }

    if (usernameInput) {
        usernameInput.addEventListener('input', function() {
            validateField(usernameInput, usernameError, 'имя пользователя');
            if (loginError) {
                loginError.classList.add('d-none');
            }
        });
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            validateField(passwordInput, passwordError, 'пароль');
            if (loginError) {
                loginError.classList.add('d-none');
            }
        });

        passwordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                submitForm();
            }
        });
    }

    if (usernameInput) {
        usernameInput.addEventListener('keypress', function(e) {
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
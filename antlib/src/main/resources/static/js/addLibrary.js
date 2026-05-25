window.addEventListener("load", init);

function init() {
    const createBtn = document.getElementById('createLibraryBtn');
    const nameInput = document.getElementById('libraryName');
    const nameError = document.getElementById('name-error');
    const modal = document.getElementById('createLibraryModal');

    function setValid(input, isValid, errorElement, message) {
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

    function validateName() {
        const name = nameInput.value.trim();

        if (!name) {
            setValid(nameInput, false, nameError, 'Введите название библиотеки');
            return false;
        }

        setValid(nameInput, true, nameError, '');
        return true;
    }

    nameInput.addEventListener('input', function() {
        validateName();
    });

    // Отправка по Enter
    nameInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            createLibrary();
        }
    });

    // Очистка формы при открытии модального окна
    if (modal) {
        modal.addEventListener('show.bs.modal', function() {
            nameInput.value = '';
            resetValidation(nameInput, nameError);
        });
    }

    async function createLibrary() {
        let isValid = validateName();
        if (!isValid) return;

        createBtn.disabled = true;
        createBtn.innerHTML = 'Создание...';

        try {
            const response = await fetch('/libraries/addLibrary', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    name: nameInput.value.trim(),
                })
            });

            const result = await response.json();

            if (result.success) {
                window.location.href = result.redirectUrl;
            } else {
                setValid(nameInput, false, nameError, result.message || 'Ошибка при создании библиотеки');
                createBtn.disabled = false;
                createBtn.innerHTML = 'Создать';
            }
        } catch (error) {
            console.error('Error:', error);
            setValid(nameInput, false, nameError, 'Ошибка соединения с сервером');
            createBtn.disabled = false;
            createBtn.innerHTML = 'Создать';
        }
    }

    createBtn.addEventListener('click', createLibrary);
}
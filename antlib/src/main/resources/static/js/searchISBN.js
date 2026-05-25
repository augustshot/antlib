window.addEventListener("load", init);

function init(){

    const isbnInput = document.getElementById('isbnInput');
    const searchBtn = document.getElementById('searchByIsbnBtn');
    const addBtn = document.getElementById('addByISBNBtn');
    const form = document.getElementById('isbnForm');
    const hiddenIsbn = document.getElementById('hiddenIsbn');
    const formText = document.getElementById('isbnFormText');

    const ISBN13_PATTERN = /^(97)(-?\d){11}$/;
    const ISBN10_PATTERN = /^(\d-?){9}\d$/;

    function cleanIsbn(isbn) {
        return isbn.replace(/[-\s]/g, '');
    }

    function isValidIsbn(isbn) {
        const trimmed = isbn.trim();
        if (!trimmed) return false;
        return ISBN13_PATTERN.test(trimmed) || ISBN10_PATTERN.test(trimmed);
    }

    function setValid(isValid, message) {
        if (isValid) {
            isbnInput.classList.remove('is-invalid');
            isbnInput.classList.add('is-valid');
            if (formText) {
                formText.classList.remove('text-danger');
                formText.classList.add('text-success');
                formText.innerHTML = message || 'ISBN корректен';
            }
        } else {
            isbnInput.classList.remove('is-valid');
            isbnInput.classList.add('is-invalid');
            if (formText) {
                formText.classList.remove('text-success');
                formText.classList.add('text-danger');
                formText.innerHTML = message || 'Некорректный формат ISBN';
            }
        }
    }

    function resetValidation() {
        isbnInput.classList.remove('is-valid', 'is-invalid');
        if (formText) {
            formText.classList.remove('text-success', 'text-danger');
            formText.innerHTML = 'Формат ISBN: 10 или 13 цифр';
        }
    }

    function submitForm() {
        const rawValue = isbnInput ? isbnInput.value : '';

        if (!rawValue.trim()) {
            setValid(false, 'Введите ISBN');
            return false;
        }

        const cleaned = cleanIsbn(rawValue);

        if (!/^\d+$/.test(cleaned)) {
            setValid(false, 'Некорректный формат ISBN');
            return false;
        }

        if (!isValidIsbn(rawValue)) {
            setValid(false, 'Некорректный формат ISBN');
            return false;
        }

        if (hiddenIsbn) {
            hiddenIsbn.value = (cleaned.length == 10 ? "978"+cleaned : cleaned);
        }
        if (isbnInput) {
            isbnInput.value = '';
        }

        form.submit();
        return true;
    }

    if (isbnInput) {
        isbnInput.addEventListener('input', function() {
            const rawValue = isbnInput.value;

            if (!rawValue.trim()) {
                resetValidation();
                return;
            }

            const cleaned = cleanIsbn(rawValue);

            if (!/^\d+$/.test(cleaned)) {
                setValid(false, 'Некорректный формат ISBN');
                return;
            }

            if (isValidIsbn(rawValue)) {
                setValid(true, 'ISBN корректен');
            } else {
                let msg = 'Некорректный формат ISBN';
                setValid(false, msg);
            }
        });

        // Отправка по Enter
        isbnInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                submitForm();
            }
        });
    }

    if (searchBtn) {
        searchBtn.addEventListener('click', function(e) {
            e.preventDefault();
            submitForm();
        });
    }

//    if (addBtn) {
//        addBtn.addEventListener('click', function() {
//            if (isbnInput) isbnInput.value = '';
//            resetValidation();
//        });
//    }
}
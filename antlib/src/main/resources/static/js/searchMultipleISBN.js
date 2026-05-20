window.addEventListener("load", init);

function init() {

    const isbnTextarea = document.getElementById('multipleIsbnInput');
    const hiddenIsbn = document.getElementById('hiddenMultipleIsbn');
    const submitBtn = document.getElementById('addMultipleSubmitBtn');
    const addBtn = document.getElementById('addMultipleBtn');
    const form = document.getElementById('addMultipleForm');
    const formText = document.getElementById('multipleIsbnFormText');

    const ISBN13_PATTERN = /^(978|979)(-?\d){10}$/;
    const ISBN10_PATTERN = /^(\d-?){9}\d$/;

    function cleanIsbn(isbn) {
        return isbn.replace(/[-\s]/g, '');
    }

    function isValidIsbn(isbn) {
        const trimmed = isbn.trim();
        if (!trimmed) return false;
        return ISBN13_PATTERN.test(trimmed) || ISBN10_PATTERN.test(trimmed);
    }

    function showInlineValidation(isValid, message) {
        if (isValid) {
            isbnTextarea.classList.remove('is-invalid');
            isbnTextarea.classList.add('is-valid');
            if (formText) {
                formText.classList.remove('text-danger');
                formText.classList.add('text-success');
                formText.innerHTML = message || 'Все ISBN корректны';
            }
        } else {
            isbnTextarea.classList.remove('is-valid');
            isbnTextarea.classList.add('is-invalid');
            if (formText) {
                formText.classList.remove('text-success');
                formText.classList.add('text-danger');
                formText.innerHTML = message || 'Некорректный формат ISBN';
            }
        }
    }

    function resetValidation() {
        isbnTextarea.classList.remove('is-valid', 'is-invalid');
        if (formText) {
            formText.classList.remove('text-success', 'text-danger');
            formText.innerHTML = 'Введите ISBN, каждый с новой строки. Формат: 10 или 13 цифр';
        }
    }

    function validateAndGetValidIsbns(rawValue) {
        const lines = rawValue.split(/\r?\n/);
        const validIsbns = [];
        const invalidIsbns = [];

        for (const line of lines) {
            const trimmed = line.trim();
            if (trimmed === '') continue;

            const cleaned = cleanIsbn(trimmed);

            if (!/^\d+$/.test(cleaned)) {
                invalidIsbns.push(trimmed);
                continue;
            }

            if (isValidIsbn(trimmed)) {
                validIsbns.push(cleaned);
            } else {
                invalidIsbns.push(trimmed);
            }
        }

        return { validIsbns, invalidIsbns };
    }

    function submitForm() {
        const rawValue = isbnTextarea ? isbnTextarea.value : '';

        if (!rawValue.trim()) {
            showInlineValidation(false, 'Введите хотя бы один ISBN');
            return false;
        }

        const { validIsbns, invalidIsbns } = validateAndGetValidIsbns(rawValue);

        if (invalidIsbns.length > 0) {
            showInlineValidation(false, 'Некорректный формат ISBN: ' + invalidIsbns.join(', '));
            return false;
        }


        if (hiddenIsbn) {
            hiddenIsbn.value = validIsbns.join('\n');
        }
        if (isbnTextarea) {
            isbnTextarea.value = '';
        }

        form.submit();
        return true;
    }

    if (isbnTextarea) {
        let debounceTimer;
        isbnTextarea.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            const rawValue = isbnTextarea.value;

            if (!rawValue.trim()) {
                resetValidation();
                return;
            }

            debounceTimer = setTimeout(() => {
                const { validIsbns, invalidIsbns } = validateAndGetValidIsbns(rawValue);

                if (invalidIsbns.length > 0) {
                    msg = invalidIsbns.join(', ');
                    msg = msg.length > 30 ? msg.slice(0, 30)+"..." : msg;
                    showInlineValidation(false, 'Некорректный формат ISBN: ' + msg);
                } else if (validIsbns.length > 0) {
                    showInlineValidation(true, 'Все ISBN корректны');
                }
            }, 500);
        });
    }

    if (submitBtn && form) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            submitForm();
        });
    }

//    if (addBtn) {
//        addBtn.addEventListener('click', function() {
//            if (isbnTextarea) isbnTextarea.value = '';
//            resetValidation();
//        });
//    }
}
window.addEventListener("load", init);

    function init(){

        const isbnTextarea = document.getElementById('isbnInput');
        const hiddenIsbn = document.getElementById('hiddenIsbn');
        const searchBtn = document.getElementById('searchByIsbnBtn');
        const addBtn = document.getElementById('addByISBNBtn');
        const form = document.getElementById('isbnForm');


        // Регулярные выражения для валидации ISBN
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

        function isOnlyDigits(str) {
            return /^\d+$/.test(str);
        }

        function showMessage(containerId, message, type) {
            const container = document.getElementById(containerId);
            if (!container) return;
            container.innerHTML = '';
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
            alertDiv.innerHTML = `${message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
            container.appendChild(alertDiv);
        }

        function clearMessages() {
            const container = document.getElementById('validationMessages');
            if (container) container.innerHTML = '';
        }

        // Привязываем событие на кнопку поиска
        if (searchBtn && form) {
            searchBtn.addEventListener('click', function(e) {
                e.preventDefault();


                const rawValue = isbnTextarea ? isbnTextarea.value : '';
                clearMessages();

                if (!rawValue.trim()) {
                    showMessage('validationMessages', 'Введите ISBN', 'danger');
                    return;
                }

                // Парсим по переносам строк
                const lines = rawValue.split(/\r?\n/);
                const validIsbns = [];
                const invalidIsbns = [];

                for (const line of lines) {
                    const trimmed = line.trim();
                    if (trimmed === '') continue;

                    const cleaned = cleanIsbn(trimmed);

                    if (!isOnlyDigits(cleaned)) {
                        invalidIsbns.push(trimmed);
                        continue;
                    }

                    if (isValidIsbn(trimmed)) {
                        validIsbns.push(cleaned);
                    } else {
                        invalidIsbns.push(trimmed);
                    }
                }

                if (invalidIsbns.length > 0) {
                    showMessage('validationMessages', 'Некорректный формат ISBN: ' + invalidIsbns.join(', '), 'danger');
                    return;
                }

                if (validIsbns.length > 1) {
                    showMessage('validationMessages', 'Найдено ' + validIsbns.length + ' ISBN. Оставьте только один ISBN', 'warning');
                    return;
                }


                if (hiddenIsbn) {
                    hiddenIsbn.value = validIsbns[0];
                }
                if (isbnTextarea) {
                    isbnTextarea.value = '';
                }

                clearMessages();
                form.submit();
            });
        }

        // Привязываем событие на кнопку открытия модального окна
        if (addBtn) {
            addBtn.addEventListener('click', function() {
                if (isbnTextarea) isbnTextarea.value = '';
                if (hiddenIsbn) hiddenIsbn.value = '';
                clearMessages();
            });
        }
    }
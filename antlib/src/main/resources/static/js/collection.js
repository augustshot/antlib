window.addEventListener("load", init);

let collectionId = null;
let bookToRemove = null;

function init() {
    collectionId = document.getElementById('collectionId')?.value;


    initEditCollection();
    initDeleteCollection();
    initAddBooksToCollection();
    initRemoveBookFromCollection();
    initModalEnterSubmit();
}

function initEditCollection() {
    const saveBtn = document.getElementById('saveCollectionBtn');
    const nameInput = document.getElementById('editCollectionName');
    const descInput = document.getElementById('editCollectionDescription');
    const errorDiv = document.getElementById('edit-name-error');

    if (!saveBtn) return;

    // Очистка ошибки при вводе
    nameInput.addEventListener('input', function() {
        errorDiv.classList.add('d-none');
        errorDiv.textContent = '';
        nameInput.classList.remove('is-invalid');
    });

    saveBtn.addEventListener('click', async function() {
        const name = nameInput.value.trim();

        // Очищаем предыдущие ошибки
        errorDiv.classList.add('d-none');
        errorDiv.textContent = '';
        nameInput.classList.remove('is-invalid');

        if (!name) {
            nameInput.classList.add('is-invalid');
            errorDiv.classList.remove('d-none');
            errorDiv.textContent = 'Введите название коллекции';
            return;
        }

        this.disabled = true;
        this.innerHTML = 'Сохранение...';

        try {
            const response = await fetch(`/collections/collection/${collectionId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    name: name,
                    description: descInput ? descInput.value.trim() : ''
                })
            });

            const result = await response.json();

            if (result.success) {
                // Успех - перезагружаем страницу
                window.location.reload();
            } else {
                // Показываем ошибку в модалке
                nameInput.classList.add('is-invalid');
                errorDiv.classList.remove('d-none');
                errorDiv.textContent = result.message || 'Ошибка при обновлении коллекции';
                this.disabled = false;
                this.innerHTML = 'Сохранить';
            }
        } catch (error) {
            console.error('Error:', error);
            nameInput.classList.add('is-invalid');
            errorDiv.classList.remove('d-none');
            errorDiv.textContent = 'Ошибка соединения с сервером';
            this.disabled = false;
            this.innerHTML = 'Сохранить';
        }
    });
}

function initDeleteCollection() {
    const deleteBtn = document.getElementById('confirmDeleteCollectionBtn');
    const modalElement = document.getElementById('deleteCollectionModal');

    if (!deleteBtn) return;

    deleteBtn.addEventListener('click', async function() {
        this.disabled = true;
        this.innerHTML = 'Удаление...';

        try {
            const response = await fetch(`/collections/collection/${collectionId}`, {
                method: 'DELETE'
            });

            const result = await response.json();

            if (result.success) {
                // Закрываем модалку и переходим к списку
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                window.location.href = '/collections';
            } else {
                // Показываем ошибку (можно добавить span для ошибки в модалке удаления)
                const errorDiv = document.getElementById('delete-collection-error');
                if (errorDiv) {
                    errorDiv.classList.remove('d-none');
                    errorDiv.textContent = result.message || 'Ошибка при удалении коллекции';
                } else {
                    alert(result.message || 'Ошибка при удалении коллекции');
                }
                this.disabled = false;
                this.innerHTML = 'Удалить';
            }
        } catch (error) {
            console.error('Error:', error);
            const errorDiv = document.getElementById('delete-collection-error');
            if (errorDiv) {
                errorDiv.classList.remove('d-none');
                errorDiv.textContent = 'Ошибка соединения с сервером';
            } else {
                alert('Ошибка соединения с сервером');
            }
            this.disabled = false;
            this.innerHTML = 'Удалить';
        }
    });
}

function initAddBooksToCollection() {
    const searchInput = document.getElementById('searchUserBooksForCollection');
    const modal = document.getElementById('addBooksToCollectionModal');
    const confirmBtn = document.getElementById('confirmAddBooksToCollectionBtn');
    const errorDiv = document.getElementById('add-books-error');

    if (!modal) return;

    // Очищаем ошибки и выделения при открытии модалки
    modal.addEventListener('show.bs.modal', function() {
        if (errorDiv) {
            errorDiv.classList.add('d-none');
            errorDiv.textContent = '';
        }
        // Снимаем выделение со всех чекбоксов
        document.querySelectorAll('.add-book-to-collection-checkbox').forEach(cb => {
            cb.checked = false;
        });
        if (searchInput) searchInput.value = '';
        loadUserBooksForCollection('');
    });

    if (searchInput) {
        searchInput.addEventListener('input', debounce(function() {
            loadUserBooksForCollection(searchInput.value);
        }, 300));
    }

    if (confirmBtn) {
        confirmBtn.addEventListener('click', async function() {
            const selected = Array.from(document.querySelectorAll('.add-book-to-collection-checkbox:checked'))
                .map(cb => cb.value);

            // Очищаем предыдущие ошибки
            if (errorDiv) {
                errorDiv.classList.add('d-none');
                errorDiv.textContent = '';
            }

            if (!selected.length) {
                if (errorDiv) {
                    errorDiv.classList.remove('d-none');
                    errorDiv.textContent = 'Выберите книги для добавления';
                }
                return;
            }

            this.disabled = true;
            this.innerHTML = 'Добавление...';

            try {
                const response = await fetch(`/collections/collection/${collectionId}/books`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ bookIds: selected })
                });

                const result = await response.json();

                if (result.success) {
                    const modalInstance = bootstrap.Modal.getInstance(modal);
                    if (modalInstance) modalInstance.hide();
                    window.location.reload();
                } else {
                    if (errorDiv) {
                        errorDiv.classList.remove('d-none');
                        errorDiv.textContent = result.message || 'Ошибка при добавлении книг';
                    }
                }
            } catch (error) {
                console.error('Error:', error);
                if (errorDiv) {
                    errorDiv.classList.remove('d-none');
                    errorDiv.textContent = 'Ошибка соединения с сервером';
                }
            } finally {
                this.disabled = false;
                this.innerHTML = 'Добавить выбранные';
            }
        });
    }
}

async function loadUserBooksForCollection(searchTerm = '') {
    const container = document.getElementById('userBooksForCollectionList');
    const errorDiv = document.getElementById('add-books-error');

    if (!container) return;

    container.innerHTML = '<div class="text-center text-muted py-3">Загрузка...</div>';

    if (errorDiv) {
        errorDiv.classList.add('d-none');
        errorDiv.textContent = '';
    }

    try {
        const response = await fetch(`/collections/${collectionId}/books?search=${encodeURIComponent(searchTerm)}`);
        const result = await response.json();

        if (result.success && result.books.length > 0) {
            container.innerHTML = result.books.map(book => `
                <div class="list-group-item">
                    <input type="checkbox" class="form-check-input me-2 add-book-to-collection-checkbox"
                           value="${book.id}" id="book_${book.id}">
                    <label for="book_${book.id}" class="cursor-pointer">
                        <strong>${escapeHtml(book.title)}</strong>
                        ${book.author ? ` - ${escapeHtml(book.author)}` : ''}
                        <span class="text-muted small ms-2">${book.isbn ? book.isbn : ''}</span>
                    </label>
                </div>
            `).join('');
        } else {
            container.innerHTML = '<div class="text-center text-muted py-3">Книги не найдены</div>';
        }
    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = '<div class="text-center text-danger py-3">Ошибка загрузки книг</div>';
    }
}

function initRemoveBookFromCollection() {
    const confirmBtn = document.getElementById('confirmRemoveBookBtn');
    const modalElement = document.getElementById('confirmRemoveBookModal');
    const errorDiv = document.getElementById('remove-book-error');

    // Обработчики для кнопок удаления (обновляются при перезагрузке таблицы)
    attachRemoveBookHandlers();

    // Наблюдатель за изменением DOM (для динамически добавленных кнопок)
    const observer = new MutationObserver(function() {
        attachRemoveBookHandlers();
    });
    observer.observe(document.querySelector('.card-body'), { childList: true, subtree: true });

    if (confirmBtn) {
        confirmBtn.addEventListener('click', async function() {
            if (!bookToRemove) return;

            // Очищаем ошибку
            if (errorDiv) {
                errorDiv.classList.add('d-none');
                errorDiv.textContent = '';
            }

            this.disabled = true;
            this.innerHTML = 'Удаление...';

            try {
                const response = await fetch(`/collections/collection/${collectionId}/books/${bookToRemove}`, {
                    method: 'DELETE'
                });

                const result = await response.json();

                if (result.success) {
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    if (modal) modal.hide();
                    window.location.reload();
                } else {
                    if (errorDiv) {
                        errorDiv.classList.remove('d-none');
                        errorDiv.textContent = result.message || 'Ошибка при удалении книги';
                    }
                }
            } catch (error) {
                console.error('Error:', error);
                if (errorDiv) {
                    errorDiv.classList.remove('d-none');
                    errorDiv.textContent = 'Ошибка соединения с сервером';
                }
            } finally {
                this.disabled = false;
                this.innerHTML = 'Удалить';
                bookToRemove = null;
            }
        });
    }
}

function attachRemoveBookHandlers() {
    document.querySelectorAll('.remove-from-collection-btn').forEach(btn => {
        // Удаляем старый обработчик, чтобы не было дублирования
        btn.removeEventListener('click', handleRemoveClick);
        btn.addEventListener('click', handleRemoveClick);
    });
}

function handleRemoveClick(e) {
    e.preventDefault();
    bookToRemove = this.dataset.bookId;
    document.getElementById('removeBookTitle').textContent = this.dataset.bookTitle;

    // Очищаем ошибку при открытии
    const errorDiv = document.getElementById('remove-book-error');
    if (errorDiv) {
        errorDiv.classList.add('d-none');
        errorDiv.textContent = '';
    }

    new bootstrap.Modal(document.getElementById('confirmRemoveBookModal')).show();
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, m => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;' }[m]));
}

function debounce(fn, delay) {
    let timer;
    return function(...args) {
        clearTimeout(timer);
        timer = setTimeout(() => fn.apply(this, args), delay);
    };
}

function initModalEnterSubmit() {
    const modals = document.querySelectorAll('.modal');

    modals.forEach(modal => {
        modal.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                e.stopPropagation();

                // Ищем активную кнопку подтверждения в этой модалке
                const confirmBtn = modal.querySelector('.btn-success:not(.btn-outline-success):not([disabled]), .btn-primary:not([disabled])');
                const deleteBtn = modal.querySelector('.btn-danger:not([disabled])');

                // Приоритет: сначала success/primary, потом danger
                const btnToClick = confirmBtn || deleteBtn;

                if (btnToClick && !btnToClick.disabled) {
                    btnToClick.click();
                }
            }
        });
    });
}
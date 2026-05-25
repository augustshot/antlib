// openlibrary-search.js

let currentSearchQuery = {};
let currentPage = 1;
const resultsPerPage = 15;

// Выполнение поиска
async function searchBooks(query, page = 1) {
    const limit = resultsPerPage;
    const offset = (page - 1) * limit;

    let url = 'https://openlibrary.org/search.json?limit=' + limit + '&offset=' + offset + '&fields=key,title,author_name,isbn,editions,cover_i,first_publish_year';

    if (query.title && query.title.trim()) {
        url += '&title=' + encodeURIComponent(query.title.trim());
    }
    if (query.author && query.author.trim()) {
        url += '&author=' + encodeURIComponent(query.author.trim());
    }

    try {
        const response = await fetch(url);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Ошибка поиска:', error);
        return null;
    }
}

// Функция для получения лучшего ISBN
function getBestIsbn(book) {
    // 1. Пытаемся взять из editions.docs
    if (book.editions && book.editions.docs && book.editions.docs.length > 0) {
        for (const edition of book.editions.docs) {
            if (edition.isbn && Array.isArray(edition.isbn) && edition.isbn.length > 0) {
                // Ищем ISBN-13
                let isbn13 = edition.isbn.find(isbn => {
                    let clean = String(isbn).replace(/-/g, '');
                    return (clean.startsWith('97')) && clean.length === 13;
                });
                if (isbn13) return isbn13;
                return edition.isbn[0];
            }
        }
    }

    // 2. Если нет в editions, пробуем внешний isbn
    if (book.isbn && Array.isArray(book.isbn) && book.isbn.length > 0) {
        // Ищем ISBN-13
        let isbn13 = book.isbn.find(isbn => {
            let clean = String(isbn).replace(/-/g, '');
            return (clean.startsWith('97'))  && clean.length === 13;
        });
        if (isbn13) return isbn13;

        // Ищем валидный ISBN-10
        let isbn10 = book.isbn.find(isbn => {
            let clean = String(isbn).replace(/-/g, '');
            return clean.length === 10 && /^\d+$/.test(clean);
        });
        if (isbn10) return isbn10;

        // Возвращаем первый
        return book.isbn[0];
    }

    return null;
}

// Создание карточки книги
function createBookCard(book) {
    const coverId = book.cover_i;
    const coverUrl = coverId
        ? 'https://covers.openlibrary.org/b/id/' + coverId + '-M.jpg'
        : null;

    const title = book.title || 'Название неизвестно';
    const author = book.author_name ? book.author_name[0] : 'Автор неизвестен';
    const firstYear = book.first_publish_year || '';

    const isbn = getBestIsbn(book);

    const col = document.createElement('div');
    col.className = 'col';

    const cardDiv = document.createElement('div');
    cardDiv.className = 'card h-100 book-card';

    // Обложка
    const coverWrapper = document.createElement('div');
    coverWrapper.className = 'cover-wrapper';
    coverWrapper.style.height = '200px';
    coverWrapper.style.overflow = 'hidden';

    if (coverUrl) {
        const img = document.createElement('img');
        img.src = coverUrl;
        img.className = 'book-cover w-100 h-100';
        img.alt = title;
        img.style.objectFit = 'cover';
        img.onerror = function() {
            this.style.display = 'none';
            const placeholder = document.createElement('div');
            placeholder.className = 'book-cover-placeholder d-flex align-items-center justify-content-center h-100';
            placeholder.textContent = '📖';
            coverWrapper.innerHTML = '';
            coverWrapper.appendChild(placeholder);
        };
        coverWrapper.appendChild(img);
    } else {
        const placeholder = document.createElement('div');
        placeholder.className = 'book-cover-placeholder d-flex align-items-center justify-content-center h-100';
        placeholder.textContent = '📖';
        coverWrapper.appendChild(placeholder);
    }
    cardDiv.appendChild(coverWrapper);

    // Тело карточки
    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';

    const titleDiv = document.createElement('div');
    titleDiv.className = 'book-title fw-bold mb-1';
    titleDiv.style.fontSize = '0.9rem';
    titleDiv.style.lineHeight = '1.3';
    titleDiv.textContent = title.length > 40 ? title.substring(0, 40) + '...' : title;
    cardBody.appendChild(titleDiv);

    const authorDiv = document.createElement('div');
    authorDiv.className = 'book-author small text-muted mb-2';
    authorDiv.textContent = author;
    cardBody.appendChild(authorDiv);

    if (firstYear) {
        const yearDiv = document.createElement('div');
        yearDiv.className = 'small text-muted';
        yearDiv.textContent = firstYear + ' г.';
        cardBody.appendChild(yearDiv);
    }

    if (isbn) {
        const isbnDiv = document.createElement('div');
        isbnDiv.className = 'small text-muted';
        isbnDiv.textContent = 'ISBN: ' + isbn;
        cardBody.appendChild(isbnDiv);
    }

    cardDiv.appendChild(cardBody);

    // Футер с кнопкой
    const cardFooter = document.createElement('div');
    cardFooter.className = 'card-footer bg-transparent border-top-0 pt-0';

    const selectBtn = document.createElement('button');
    selectBtn.className = 'btn btn-sm btn-outline-success w-100 select-book-btn';
    selectBtn.innerHTML = 'Выбрать';
    selectBtn.addEventListener('click', (e) => {
        e.preventDefault();
        selectBook({
            title: title,
            author: author,
            cover: coverUrl,
            year: firstYear,
            isbn: isbn ? String(isbn).replace(/-/g, '') : ''
        });
    });

    cardFooter.appendChild(selectBtn);
    cardDiv.appendChild(cardFooter);

    col.appendChild(cardDiv);
    return col;
}

// Выбор книги - сохраняем в БД и заполняем форму
async function selectBook(book) {
    // Показываем индикатор загрузки
    const selectBtn = event?.target;
    if (selectBtn) {
        selectBtn.disabled = true;
        selectBtn.innerHTML = 'Сохранение...';
    }

    try {
        // 1. Сохраняем книгу в БД через API
        const response = await fetch('/books/saveFromSearch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                title: book.title,
                author: book.author,
                isbn: book.isbn,
                cover: book.cover,
                year: book.year,
                verified: true
            })
        });

        const result = await response.json();

        if (result.success) {
            // 2. Заполняем форму полученными данными
            const titleInput = document.getElementById('title');
            const authorInput = document.getElementById('author');
            const coverInput = document.getElementById('cover');
            const yearInput = document.getElementById('year');
            const isbnInput = document.getElementById('ISBN');
            const bookIdInput = document.getElementById('bookDescriptionId');

            if (titleInput) titleInput.value = result.book.title || '';
            if (authorInput) authorInput.value = result.book.author || '';
            if (coverInput) coverInput.value = result.book.cover || '';
            if (yearInput) yearInput.value = result.book.year || '';
            if (isbnInput) isbnInput.value = result.book.isbn || '';
            if (bookIdInput) bookIdInput.value = result.book.id;

        }
    } catch (error) {
        console.error('Error saving book:', error);
    } finally {
        if (selectBtn) {
            selectBtn.disabled = false;
            selectBtn.innerHTML = 'Выбрать';
        }
    }

    // Закрыть модальное окно
    const modalElement = document.getElementById('searchModal');
    if (modalElement) {
        const modal = bootstrap.Modal.getInstance(modalElement);
        if (modal) modal.hide();
    }
}

// Отображение пагинации
function renderPagination(totalPages, currentPage) {
    const paginationDiv = document.getElementById('searchPagination');
    if (!paginationDiv) return;

    if (totalPages <= 1) {
        paginationDiv.innerHTML = '';
        return;
    }

    let html = '<ul class="pagination pagination-sm">';

    html += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link text-success" href="#" data-page="${currentPage - 1}">&laquo; Назад</a>
             </li>`;

    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    for (let i = startPage; i <= endPage; i++) {
        html += `<li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link ${i === currentPage ? 'active-page' : 'text-success'}" href="#" data-page="${i}">${i}</a>
                 </li>`;
    }

    html += `<li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link text-success" href="#" data-page="${currentPage + 1}">Далее &raquo;</a>
             </li>`;
    html += '</ul>';

    paginationDiv.innerHTML = html;

    paginationDiv.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', async (e) => {
            e.preventDefault();
            const page = parseInt(link.dataset.page);
            if (page && !isNaN(page) && page !== currentPage && page >= 1 && page <= totalPages) {
                currentPage = page;
                await performSearch(currentPage);
            }
        });
    });
}

// Основная функция поиска
// Основная функция поиска
async function performSearch(page = 1) {
    const title = document.getElementById('searchTitle')?.value || '';
    const author = document.getElementById('searchAuthor')?.value || '';

    if (!title && !author) {
        alert('Введите название или автора для поиска');
        return;
    }

    currentSearchQuery = { title, author };
    currentPage = page;

    const loadingDiv = document.getElementById('searchLoading');
    const resultsDiv = document.getElementById('searchResults');
    const emptyDiv = document.getElementById('searchEmpty');

    if (loadingDiv) loadingDiv.style.display = 'block';
    if (resultsDiv) resultsDiv.style.display = 'none';
    if (emptyDiv) emptyDiv.style.display = 'none';

    const data = await searchBooks(currentSearchQuery, page);

    if (loadingDiv) loadingDiv.style.display = 'none';

    if (!data || !data.docs || data.docs.length === 0) {
        if (emptyDiv) emptyDiv.style.display = 'block';
        return;
    }

    const resultsGrid = document.getElementById('searchResultsGrid');
    if (resultsGrid) {
        resultsGrid.innerHTML = '';

        // Создаём строки и карточки
        data.docs.forEach(book => {
            const card = createBookCard(book);
            resultsGrid.appendChild(card);
        });
    }

    const resultCount = document.getElementById('resultCount');
    if (resultCount) resultCount.textContent = data.numFound + ' книг найдено';

    if (resultsDiv) resultsDiv.style.display = 'block';

    const totalPages = Math.ceil(data.numFound / resultsPerPage);
    renderPagination(totalPages, page);
}

// Инициализация
document.addEventListener('DOMContentLoaded', function() {
    const searchBtn = document.getElementById('searchBooksBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', () => performSearch(1));
    }

    const titleInput = document.getElementById('searchTitle');
    const authorInput = document.getElementById('searchAuthor');

    if (titleInput) {
        titleInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') performSearch(1);
        });
    }
    if (authorInput) {
        authorInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') performSearch(1);
        });
    }

    // Очистка при открытии модалки
    const searchModal = document.getElementById('searchModal');
    if (searchModal) {
        searchModal.addEventListener('show.bs.modal', function() {
            const titleField = document.getElementById('searchTitle');
            const authorField = document.getElementById('searchAuthor');
            if (titleField) titleField.value = '';
            if (authorField) authorField.value = '';

            const resultsDiv = document.getElementById('searchResults');
            const loadingDiv = document.getElementById('searchLoading');
            const emptyDiv = document.getElementById('searchEmpty');
            const resultsGrid = document.getElementById('searchResultsGrid');

            if (resultsDiv) resultsDiv.style.display = 'none';
            if (loadingDiv) loadingDiv.style.display = 'none';
            if (emptyDiv) emptyDiv.style.display = 'none';
            if (resultsGrid) resultsGrid.innerHTML = '';
        });
    }
});
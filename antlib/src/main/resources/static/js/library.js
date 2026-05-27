// Глобальные переменные
let isOwner = false;
let libraryId = null;
let currentRoomId = null;
let shelvesData = [];
let isEditMode = false;
let userBooks = [];
let currentShelfId = null;
let currentSearchResults = [];
let isSearchActive = false;

const SLOT_SIZE_PX = 250;
const GRID_SLOTS_W = 10;
const GRID_SLOTS_H = 10;

const SHELF_UNIT_SIZE = 20;
const DEFAULT_BOOKS_PER_UNIT = 10; // 10 книг на единицу ширины по умолчанию
const MAX_BOOKS_PER_UNIT = 15;      // максимум 15 книг на единицу ширины
const MIN_BOOK_WIDTH = 8;
const GAP_BETWEEN_BOOKS = 0.5;

const DEFAULT_ZOOM = 0.8;
const MIN_ZOOM = 0.4;
const MAX_ZOOM = 2.0;

// Камера
let camera = { x: 0, y: 0, scale: 1 };
let isPanning = false;
let panStartX = 0, panStartY = 0;
let panCameraStartX = 0, panCameraStartY = 0;

// Drag&Drop полок
let isDraggingShelf = false;
let draggedShelfId = null;
let dragStartX = 0, dragStartY = 0;
let dragOriginalSlotX = 0, dragOriginalSlotY = 0;
let dragOriginalWidthSlots = 0, dragOriginalHeightSlots = 0;

document.addEventListener('DOMContentLoaded', init);

function init() {
    libraryId = document.getElementById('libraryId')?.value;
    isOwner = document.getElementById('isOwner')?.value === 'true';

    if (!libraryId) return;

    // Контейнер для уведомлений
    if (!document.getElementById('notificationContainer')) {
        const container = document.createElement('div');
        container.id = 'notificationContainer';
        container.className = 'notification-container';
        document.body.appendChild(container);
    }


    if (sessionStorage.getItem('openMembersModal') === 'true') {
            sessionStorage.removeItem('openMembersModal');

            setTimeout(() => {
                const membersModalEl = document.getElementById('membersModal');
                if (membersModalEl) {
                    const modal = new bootstrap.Modal(membersModalEl);
                    modal.show();
                    showNotification('Список участников обновлен', 'success');
                }
            }, 100);
        }

    initBasicButtons();
    initMembersButtons();
    initConfirmRemoveMember();
    initLeaveLibrary();
    initEditLibraryButtons();
    initDeleteLibraryButtons();
    initCreateRoom();
    initRegenerateCode();

    initRoomClickHandlers();
    initViewModes();
    initRoomActionButtons();
    initZoomAndPan();
    initShelvesModals();
    initAddShelfModal();
    initDragAndDrop();
    initSearch();
    initModalEnterSubmit();
}

// --- УВЕДОМЛЕНИЯ ---
function showNotification(message, type = 'success') {
    const container = document.getElementById('notificationContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `custom-toast ${type}`;

    let icon = '✓';
    if (type === 'error') icon = '✕';
    if (type === 'warning') icon = '!';

    toast.innerHTML = `
        <span style="font-weight:bold; margin-right:10px;">${icon}</span>
        <span>${message}</span>
        <button type="button" style="background:none;border:none;font-size:16px;cursor:pointer;margin-left:auto;" onclick="this.parentElement.remove()">×</button>
    `;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// --- БАЗОВЫЕ ФУНКЦИИ ---
function initBasicButtons() {
    const copyBtn = document.getElementById('copyCodeBtn');
    if (copyBtn) {
        copyBtn.addEventListener('click', function() {
            const codeInput = document.getElementById('libraryCode');
            if (codeInput) {
                codeInput.select();
                document.execCommand('copy');
                showNotification('Код скопирован');
            }
        });
    }
}

let userIdToRemove = null;
function initMembersButtons() {
    // Получаем экземпляр модалки участников (создаём один раз)
    const membersModalEl = document.getElementById('membersModal');
    if (membersModalEl) {
        membersModalInstance = new bootstrap.Modal(membersModalEl);
    }

    document.querySelectorAll('.remove-member-btn').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            userIdToRemove = this.dataset.userId;
            const username = this.dataset.username;
            document.getElementById('removeMemberName').textContent = username;

            // Скрываем модалку участников
            if (membersModalInstance) {
                membersModalInstance.hide();
            }

            // Показываем модалку подтверждения
            const confirmModal = new bootstrap.Modal(document.getElementById('confirmRemoveMemberModal'));
            confirmModal.show();
        });
    });
}

function initConfirmRemoveMember() {
    const confirmBtn = document.getElementById('confirmRemoveMemberBtn');
    const confirmModalElement = document.getElementById('confirmRemoveMemberModal');

    if (!confirmBtn) return;

    // При закрытии модалки подтверждения (любым способом) - показываем модалку участников
    if (confirmModalElement) {
        confirmModalElement.addEventListener('hidden.bs.modal', function() {
            // Показываем модалку участников снова
            if (membersModalInstance) {
                membersModalInstance.show();
            }
            // Сбрасываем userId
            userIdToRemove = null;
        });
    }

    confirmBtn.addEventListener('click', async function() {
        if (!userIdToRemove) return;

        this.disabled = true;
        const originalText = this.innerHTML;
        this.innerHTML = 'Удаление...';

        try {
            const res = await fetch(`/libraries/${libraryId}/removeMember`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId: userIdToRemove,
                    action: 'remove'
                })
            });
            const result = await res.json();

            if (result.success) {
                showNotification('Участник удален');
                sessionStorage.setItem('openMembersModal', 'true');
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            } else {
                showNotification(result.message || 'Ошибка', 'error');
                this.disabled = false;
                this.innerHTML = originalText;
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('Ошибка соединения', 'error');
            this.disabled = false;
            this.innerHTML = originalText;
        }
    });
}

function initLeaveLibrary() {
    const leaveBtn = document.getElementById('leaveLibraryBtn');
    if (!leaveBtn) return;

    leaveBtn.addEventListener('click', () => {
        const modal = new bootstrap.Modal(document.getElementById('confirmLeaveModal'));
        modal.show();
    });

    const confirmLeaveBtn = document.getElementById('confirmLeaveBtn');
    if (confirmLeaveBtn) {
        confirmLeaveBtn.addEventListener('click', async function() {
            this.disabled = true;
            this.innerHTML = 'Выход...';

            try {
                const res = await fetch(`/libraries/${libraryId}/removeMember`, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                                            action: 'leave'
                                        })
                });
                const result = await res.json();

                if (result.success) {
                    showNotification('Вы вышли из библиотеки', 'success');
                    window.location.href = '/libraries';
                } else {
                    showNotification(result.message || 'Ошибка при выходе', 'error');
                    this.disabled = false;
                    this.innerHTML = 'Выйти';
                }
            } catch (error) {
                console.error('Error:', error);
                showNotification('Ошибка соединения', 'error');
                this.disabled = false;
                this.innerHTML = 'Выйти';
            }
        });
    }
}

function initEditLibraryButtons() {
    const saveBtn = document.getElementById('saveLibraryBtn');
    const nameInput = document.getElementById('editLibraryName');
    const errorDiv = document.getElementById('edit-name-error');

    if (!saveBtn) return;
    saveBtn.addEventListener('click', async function() {
        const name = nameInput.value.trim();
        if (!name) {
            errorDiv.classList.remove('d-none');
            errorDiv.textContent = 'Введите название';
            return;
        }
        this.disabled = true;
        try {
            const res = await fetch(`/libraries/${libraryId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name })
            });
            const result = await res.json();
            if (result.success) {
                showNotification('Библиотека обновлена');
                window.location.reload();
            } else {
                showNotification(result.message, 'error');
                this.disabled = false;
            }
        } catch (error) {
            showNotification('Ошибка соединения', 'error');
            this.disabled = false;
        }
    });
}

function initDeleteLibraryButtons() {
    const deleteBtn = document.getElementById('confirmDeleteBtn');
    if (!deleteBtn) return;
    deleteBtn.addEventListener('click', async function() {
        this.disabled = true;
        try {
            const res = await fetch(`/libraries/${libraryId}`, { method: 'DELETE' });
            const result = await res.json();
            if (result.success) {
                showNotification('Библиотека удалена');
                window.location.href = '/libraries';
            } else {
                showNotification(result.message, 'error');
                this.disabled = false;
            }
        } catch (error) {
            showNotification('Ошибка соединения', 'error');
            this.disabled = false;
        }
    });
}

function initCreateRoom() {
    const btn = document.getElementById('createRoomBtn');
    const nameInput = document.getElementById('roomName');
    const errorDiv = document.getElementById('room-name-error');
    if (!btn) return;

    btn.addEventListener('click', async function() {
        const name = nameInput.value.trim();
        if (!name) {
            errorDiv.classList.remove('d-none');
            errorDiv.textContent = 'Введите название';
            return;
        }
        this.disabled = true;
        try {
            const res = await fetch(`/libraries/${libraryId}/addRoom`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name })
            });
            const result = await res.json();
            if (result.success) {
                showNotification('Комната создана');
                window.location.reload();
            } else {
                showNotification(result.message, 'error');
                this.disabled = false;
            }
        } catch (error) {
            showNotification('Ошибка соединения', 'error');
            this.disabled = false;
        }
    });
}

function initRegenerateCode() {
    const btn = document.getElementById('regenerateCodeBtn');
    if (!btn) return;
    btn.addEventListener('click', async function() {
        this.disabled = true;
        try {
            const res = await fetch(`/libraries/${libraryId}/regenerateCode`, { method: 'POST' });
            const result = await res.json();
            if (result.success) {
                document.getElementById('libraryCode').value = result.inviteCode;
                showNotification('Код обновлен');
            } else {
                showNotification(result.message, 'error');
            }
        } catch (error) {
            showNotification('Ошибка соединения', 'error');
        } finally { this.disabled = false; }
    });
}

// --- КОМНАТЫ ---
function initRoomClickHandlers() {
    const items = document.querySelectorAll('.room-item');

    // 1. Проверяем, есть ли вообще комнаты в списке
    if (!items.length) {
        const header = document.getElementById('roomNameHeader');
        if (header) header.textContent = "Выберите комнату";

        // Скрываем кнопки управления, так как комнаты нет
        const viewBtn = document.getElementById('viewModeBtn');
        const editBtn = document.getElementById('editModeBtn');
        const zoomGroup = document.querySelector('.btn-group-sm.me-2');
        const addShelfBtn = document.getElementById('openAddShelfModalBtn');
        const saveLayoutBtn = document.getElementById('saveLayoutBtn');
        const cancelLayoutBtn = document.getElementById('cancelLayoutBtn');

        if (viewBtn) viewBtn.style.display = 'none';
        if (editBtn) editBtn.style.display = 'none';
        if (zoomGroup) zoomGroup.style.display = 'none';
        if (addShelfBtn) addShelfBtn.style.display = 'none';
        if (saveLayoutBtn) saveLayoutBtn.style.display = 'none';
        if (cancelLayoutBtn) cancelLayoutBtn.style.display = 'none';

        return; // Прерываем выполнение, комнат нет
    }

    // 2. Если комнаты есть, вешаем обработчики кликов
    items.forEach(item => {
        item.addEventListener('click', function(e) {
            if (e.target.closest('.room-actions')) return;
            const roomId = this.querySelector('.room-id')?.value;
            const roomName = this.querySelector('.room-name')?.value;
            if (!roomId) return;

            document.querySelectorAll('.room-item').forEach(ri => ri.classList.remove('active'));
            this.classList.add('active');
            loadRoom(roomId, roomName);
        });
    });

    // 3. Автоматически выбираем первую комнату
    const first = document.querySelector('.room-item');
    if (first) {
        const roomId = first.querySelector('.room-id')?.value;
        const roomName = first.querySelector('.room-name')?.value;
        if (roomId) {
            first.classList.add('active');
            loadRoom(roomId, roomName);
        }
    }
}

async function loadRoom(roomId, roomName) {
    const header = document.getElementById('roomNameHeader');
    if (header) header.textContent = roomName || 'Комната';

    try {
        const res = await fetch(`/libraries/${libraryId}/rooms/${roomId}`, {method: 'GET'});
        const result = await res.json();
        if (result.success) {
            shelvesData = result.shelves || [];
            // Сброс флагов удаления при загрузке
            shelvesData.forEach(s => s.markedForDeletion = false);

            if (!isEditMode) {
                for (let shelf of shelvesData) {
                    const booksRes = await fetch(`/libraries/${libraryId}/shelves/${shelf.id}`);
                    const booksResult = await booksRes.json();
                    shelf.books = booksResult.success ? booksResult.books : [];
                }
            }
            initRoomCanvas(roomId);
        }
    } catch(e) {
        console.error(e);
        showNotification('Ошибка загрузки комнаты', 'error');
    }
}

function initRoomCanvas(roomId) {
    const canvas = document.getElementById('roomCanvas');
    if (!canvas) return;
    currentRoomId = roomId;
    canvas.innerHTML = '';

    const totalWidth = GRID_SLOTS_W * SLOT_SIZE_PX;
    const totalHeight = GRID_SLOTS_H * SLOT_SIZE_PX;
    canvas.style.width = `${totalWidth}px`;
    canvas.style.height = `${totalHeight}px`;

    // Сетка
    for (let y = 0; y < GRID_SLOTS_H; y++) {
        for (let x = 0; x < GRID_SLOTS_W; x++) {
            const slot = document.createElement('div');
            slot.className = 'slot';
            slot.style.left = `${x * SLOT_SIZE_PX}px`;
            slot.style.top = `${y * SLOT_SIZE_PX}px`;
            slot.style.width = `${SLOT_SIZE_PX}px`;
            slot.style.height = `${SLOT_SIZE_PX}px`;
            canvas.appendChild(slot);
        }
    }
    drawShelves();
}


function drawShelves() {
    const canvas = document.getElementById('roomCanvas');
    if (!canvas) return;

    document.querySelectorAll('.shelf').forEach(el => el.remove());

    for (const shelf of shelvesData) {
        if (shelf.markedForDeletion && isEditMode) continue;

        const div = document.createElement('div');
        div.className = 'shelf';
        div.dataset.id = shelf.id;

        const widthSlots = Math.ceil(shelf.width / SHELF_UNIT_SIZE);
        const heightSlots = Math.ceil(shelf.height / SHELF_UNIT_SIZE);
        const leftPx = shelf.positionX * SLOT_SIZE_PX;
        const topPx = shelf.positionY * SLOT_SIZE_PX;
        const widthPx = widthSlots * SLOT_SIZE_PX;
        const heightPx = heightSlots * SLOT_SIZE_PX;

        div.style.left = `${leftPx}px`;
        div.style.top = `${topPx}px`;
        div.style.width = `${widthPx}px`;
        div.style.height = `${heightPx}px`;
        div.style.backgroundColor = isEditMode ? 'rgba(245,240,232,0.8)' : '#f5f0e8';
        div.style.gap = `${GAP_BETWEEN_BOOKS}px`;

        if (isEditMode && isOwner) {
            // Режим редактирования - хендлы
            div.innerHTML = `
                <div class="shelf-handle drag-handle">✥</div>
                        <div class="shelf-handle resize-handle">⣿</div>
                        <div class="shelf-handle delete-handle">×</div>
            `;

            const dragHandle = div.querySelector('.drag-handle');
            const resizeHandle = div.querySelector('.resize-handle');
            const deleteHandle = div.querySelector('.delete-handle');

            if (dragHandle) {
                dragHandle.addEventListener('mousedown', (e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    startDrag(e, shelf.id, widthSlots, heightSlots);
                });
            }
            if (resizeHandle) {
                resizeHandle.addEventListener('click', (e) => {
                    e.stopPropagation();
                    openResizeShelfModal(shelf.id);
                });
            }
            if (deleteHandle) {
                deleteHandle.addEventListener('click', (e) => {
                    e.stopPropagation();
                    markShelfForDeletion(shelf.id);
                });
            }
        } else {
            // Режим просмотра - книги
            const books = shelf.books || [];
            const capacity = shelf.capacity;
            const availableWidth = widthPx - 8;
            const gap = GAP_BETWEEN_BOOKS;

            // Отображаем книги в количестве не больше вместимости
            const displayBooks = books.slice(0, capacity);
            const bookCount = displayBooks.length;

            // Рассчитываем ширину книги
            const MAX_BOOK_WIDTH_FRACTION = 10; // 1/10 ширины слота
            const maxBookWidth = SLOT_SIZE_PX / MAX_BOOK_WIDTH_FRACTION; // 250 / 10 = 25px
            let bookWidth = MIN_BOOK_WIDTH;
            if (bookCount > 0) {
                const totalGapWidth = (bookCount - 1) * gap;
                const calculatedWidth = (availableWidth - totalGapWidth) / bookCount;
                bookWidth = Math.min(maxBookWidth, Math.max(MIN_BOOK_WIDTH, Math.floor(calculatedWidth)));
            }

            const bookHeight = SLOT_SIZE_PX - 8;

            // Отрисовка книг
            displayBooks.forEach((book) => {
                const bookDiv = document.createElement('div');
                bookDiv.className = 'book-on-shelf';
                bookDiv.style.width = `${bookWidth}px`;
                bookDiv.style.height = `${bookHeight}px`;
                bookDiv.title = `${book.title}\n${book.author || ''}`;
                bookDiv.dataset.shelfBookId = book.shelfBookId;  // ← ДОБАВИТЬ ЭТУ СТРОКУ
                bookDiv.addEventListener('click', (e) => {
                    e.stopPropagation();
                    showBookPopup(book, e.clientX, e.clientY);
                });
                div.appendChild(bookDiv);
            });

            // Пустая полка - показываем кнопку добавления
            if (books.length === 0 && isOwner) {
                const emptyDiv = document.createElement('div');
                emptyDiv.className = 'empty-shelf';
                emptyDiv.style.height = `${bookHeight}px`;
                emptyDiv.textContent = '+';
                emptyDiv.addEventListener('click', (e) => {
                    e.stopPropagation();
                    currentShelfId = shelf.id;
                    loadUserBooks();
                    new bootstrap.Modal(document.getElementById('addBooksToShelfModal')).show();
                });
                div.appendChild(emptyDiv);
            }

            // Карандашик для управления книгами
            if (isOwner) {
                const editHandle = document.createElement('div');
                editHandle.className = 'shelf-handle edit-books-handle';

                editHandle.innerHTML = '✎';
                editHandle.title = "Управление книгами";
                editHandle.addEventListener('click', (e) => {
                    e.stopPropagation();
                    currentShelfId = shelf.id;
                    loadShelfBooks(currentShelfId);
                });
                div.appendChild(editHandle);
            }
        }
        canvas.appendChild(div);
    }
}

function markShelfForDeletion(shelfId) {
    const shelf = shelvesData.find(s => s.id === shelfId);
    if (shelf) {
        shelf.markedForDeletion = true;
        drawShelves();
        showNotification('Полка будет удалена после сохранения', 'warning');
    }
}

// --- РЕЖИМЫ И СОХРАНЕНИЕ ---
function initViewModes() {
    const viewBtn = document.getElementById('viewModeBtn');
    const editBtn = document.getElementById('editModeBtn');
    const addShelfBtn = document.getElementById('openAddShelfModalBtn');
    const saveLayoutBtn = document.getElementById('saveLayoutBtn');

    // Создаем кнопку отмены динамически, если её нет в HTML
    let cancelLayoutBtn = document.getElementById('cancelLayoutBtn');
    if (!cancelLayoutBtn && isOwner) {
        cancelLayoutBtn = document.createElement('button');
        cancelLayoutBtn.id = 'cancelLayoutBtn';
        cancelLayoutBtn.className = 'btn btn-sm btn-outline-secondary d-none me-2';
        cancelLayoutBtn.innerHTML = 'Отмена';
        // Вставляем перед кнопкой сохранения
        if (saveLayoutBtn) saveLayoutBtn.parentNode.insertBefore(cancelLayoutBtn, saveLayoutBtn);
    }

    if (!isOwner) {
        [viewBtn, editBtn, addShelfBtn, saveLayoutBtn, cancelLayoutBtn].forEach(btn => { if(btn) btn.style.display = 'none'; });
        isEditMode = false;
        if (currentRoomId) loadRoom(currentRoomId, document.getElementById('roomNameHeader')?.innerText);
        return;
    }

    function setMode(edit) {
        isEditMode = edit;
        if (addShelfBtn) addShelfBtn.classList.toggle('d-none', !edit);
        if (saveLayoutBtn) saveLayoutBtn.classList.toggle('d-none', !edit);
        if (cancelLayoutBtn) cancelLayoutBtn.classList.toggle('d-none', !edit);

        if (viewBtn && editBtn) {
            if (edit) {
                viewBtn.className = 'btn btn-outline-success';
                editBtn.className = 'btn btn-success';
            } else {
                viewBtn.className = 'btn btn-success';
                editBtn.className = 'btn btn-outline-success';
            }
        }

        if (!edit && currentRoomId) {
             // При выходе в просмотр перезагружаем данные, чтобы сбросить несохраненные изменения
             loadRoom(currentRoomId, document.getElementById('roomNameHeader')?.innerText);
        } else {
             drawShelves();
        }
    }

    if (viewBtn) viewBtn.addEventListener('click', () => setMode(false));
    if (editBtn) editBtn.addEventListener('click', () => setMode(true));

    // Кнопка Отмена
    if (cancelLayoutBtn) {
        cancelLayoutBtn.addEventListener('click', () => {
            setMode(false); // Переключит в просмотр и перезагрузит данные
                            showNotification('Изменения отменены', 'warning');
        });
    }

    // Кнопка Сохранить
    if (saveLayoutBtn) {
        saveLayoutBtn.addEventListener('click', async () => {
            saveLayoutBtn.disabled = true;
            const originalText = saveLayoutBtn.innerHTML;
            saveLayoutBtn.innerHTML = ' Сохранение...';

            try {
                const idsToDelete = shelvesData.filter(s => s.markedForDeletion).map(s => s.id);
                const shelvesToSend = shelvesData
                    .filter(s => !s.markedForDeletion)
                    .map(s => ({
                        id: s.id,
                        width: s.width,
                        height: s.height,
                        positionX: s.positionX,
                        positionY: s.positionY,
                        capacity: s.capacity
                    }));

                await fetch(`/libraries/${libraryId}/rooms/${currentRoomId}/saveShelves`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        shelves: shelvesToSend,
                        deletedIds: idsToDelete
                    })
                });

                showNotification('Расстановка сохранена');

                // Удаляем помеченные из локального массива
                shelvesData = shelvesData.filter(s => !s.markedForDeletion);

                saveLayoutBtn.innerHTML = 'Сохранено!';
                setTimeout(() => {
                    saveLayoutBtn.innerHTML = originalText;
                    saveLayoutBtn.disabled = false;
                }, 2000);

            } catch (e) {
                console.error(e);
                showNotification('Ошибка сохранения', 'error');
                saveLayoutBtn.disabled = false;
                saveLayoutBtn.innerHTML = originalText;
            }
        });
    }

    setMode(false);
}

function markShelfForDeletion(shelfId) {
    const shelf = shelvesData.find(s => s.id === shelfId);
    if (shelf) {
        shelf.markedForDeletion = true;
        drawShelves();
        showNotification('Полка будет удалена после сохранения', 'warning');
    }
}

// --- ДОБАВЛЕНИЕ И ИЗМЕНЕНИЕ РАЗМЕРА ---
// Функция добавления полки с проверкой вместимости
function addShelf(width, height, capacity) {
    const widthSlots = Math.ceil(width / SHELF_UNIT_SIZE);
    const heightSlots = Math.ceil(height / SHELF_UNIT_SIZE);
    const maxCapacity = (width / SHELF_UNIT_SIZE) * MAX_BOOKS_PER_UNIT;

    // Проверка вместимости
    if (capacity > maxCapacity) {
        showNotification(`Вместимость не может превышать ${maxCapacity} книг (максимум 15 на клетку ширины)`, 'error');
        return false;
    }

    if (widthSlots > GRID_SLOTS_W || heightSlots > GRID_SLOTS_H) {
        showNotification(`Полка слишком большая! Максимум: ${GRID_SLOTS_W}x${GRID_SLOTS_H} клеток`, 'error');
        return false;
    }

    for (let y = 0; y <= GRID_SLOTS_H - heightSlots; y++) {
        for (let x = 0; x <= GRID_SLOTS_W - widthSlots; x++) {
            let collision = false;
            for (const s of shelvesData) {
                if (s.markedForDeletion) continue;
                const sWidthSlots = Math.ceil(s.width / SHELF_UNIT_SIZE);
                const sHeightSlots = Math.ceil(s.height / SHELF_UNIT_SIZE);
                if (x < s.positionX + sWidthSlots && x + widthSlots > s.positionX &&
                    y < s.positionY + sHeightSlots && y + heightSlots > s.positionY) {
                    collision = true;
                    break;
                }
            }
            if (!collision) {
                shelvesData.push({
                    width: width,
                    height: height,
                    positionX: x,
                    positionY: y,
                    capacity: capacity,
                    books: [],
                    markedForDeletion: false
                });
                drawShelves();
                showNotification('Полка добавлена');
                return true;
            }
        }
    }
    showNotification('Нет свободного места для полки такого размера', 'error');
    return false;
}

// Функция изменения размера полки с проверкой вместимости
function resizeShelf(id, newWidth, newHeight, newCapacity) {
    const shelf = shelvesData.find(s => s.id === id);
    if (!shelf) return;

    const newWidthSlots = Math.ceil(newWidth / SHELF_UNIT_SIZE);
    const newHeightSlots = Math.ceil(newHeight / SHELF_UNIT_SIZE);
    const maxCapacity = (newWidth / SHELF_UNIT_SIZE) * MAX_BOOKS_PER_UNIT;

    // Проверка вместимости
    if (newCapacity > maxCapacity) {
        showNotification(`Вместимость не может превышать ${maxCapacity} книг (максимум 15 на клетку ширины)`, 'error');
        return;
    }

    if (newWidthSlots > GRID_SLOTS_W || newHeightSlots > GRID_SLOTS_H) {
        showNotification('Новый размер превышает границы поля', 'error');
        return;
    }

    let newX = shelf.positionX;
    let newY = shelf.positionY;

    if (newX + newWidthSlots > GRID_SLOTS_W) newX = GRID_SLOTS_W - newWidthSlots;
    if (newY + newHeightSlots > GRID_SLOTS_H) newY = GRID_SLOTS_H - newHeightSlots;
    if (newX < 0) newX = 0;
    if (newY < 0) newY = 0;

    let collision = false;
    for (const other of shelvesData) {
        if (other.id === id) continue;
        const otherWSlots = Math.ceil(other.width / SHELF_UNIT_SIZE);
        const otherHSlots = Math.ceil(other.height / SHELF_UNIT_SIZE);
        if (newX < other.positionX + otherWSlots && newX + newWidthSlots > other.positionX &&
            newY < other.positionY + otherHSlots && newY + newHeightSlots > other.positionY) {
            collision = true;
            break;
        }
    }

    if (!collision) {
        shelf.width = newWidth;
        shelf.height = newHeight;
        shelf.positionX = newX;
        shelf.positionY = newY;
        shelf.capacity = newCapacity;
        drawShelves();
        showNotification('Размер изменен');
    } else {
        showNotification('Недостаточно места (пересечение с другой полкой)', 'error');
    }
}

// --- DRAG AND DROP & PAN ---
// Перетаскивание полок и панорамирование
function initDragAndDrop() {
    const viewport = document.getElementById('roomViewport');

    // Обработчик начала перетаскивания (решаем что тащим)
    const onMouseDown = (e) => {
        // Проверяем, не кликнули ли по хендлу полки
        const isDragHandle = e.target.closest('.drag-handle');
        const isShelf = e.target.closest('.shelf');

        if (isDragHandle && isEditMode && isOwner) {
            // Начинаем перетаскивание полки
            e.preventDefault();
            const shelfEl = e.target.closest('.shelf');
            if (shelfEl) {
                const shelfId = parseInt(shelfEl.dataset.id);
                const shelf = shelvesData.find(s => s.id === shelfId);
                if (shelf) {
                    const widthSlots = Math.ceil(shelf.width / SHELF_UNIT_SIZE);
                    const heightSlots = Math.ceil(shelf.height / SHELF_UNIT_SIZE);
                    startDrag(e, shelfId, widthSlots, heightSlots);
                }
            }
        } else if (isShelf && isEditMode && isOwner) {
            // Ничего не делаем, чтобы не мешать кликам по другим элементам
            return;
        } else {
            // Иначе начинаем панорамирование поля
            isPanning = true;
            panStartX = e.clientX;
            panStartY = e.clientY;
            panCameraStartX = camera.x;
            panCameraStartY = camera.y;
            if (viewport) viewport.style.cursor = 'grabbing';
        }
    };

    // Обработчик движения мыши
    const onMouseMove = (e) => {
        // Если тащим полку
        if (isDraggingShelf && draggedShelfId !== null && isEditMode && isOwner) {
            const shelf = shelvesData.find(s => s.id === draggedShelfId);
            if (!shelf) return;

            const deltaX = e.clientX - dragStartX;
            const deltaY = e.clientY - dragStartY;
            const deltaSlotsX = Math.round(deltaX / SLOT_SIZE_PX);
            const deltaSlotsY = Math.round(deltaY / SLOT_SIZE_PX);

            let newX = dragOriginalSlotX + deltaSlotsX;
            let newY = dragOriginalSlotY + deltaSlotsY;

            newX = Math.min(Math.max(0, newX), GRID_SLOTS_W - dragOriginalWidthSlots);
            newY = Math.min(Math.max(0, newY), GRID_SLOTS_H - dragOriginalHeightSlots);

            // Проверка коллизий
            let hasCollision = false;
            for (const other of shelvesData) {
                if (other.id === draggedShelfId) continue;
                if (other.markedForDeletion) continue;
                const otherWSlots = Math.ceil(other.width / SHELF_UNIT_SIZE);
                const otherHSlots = Math.ceil(other.height / SHELF_UNIT_SIZE);
                if (newX < other.positionX + otherWSlots && newX + dragOriginalWidthSlots > other.positionX &&
                    newY < other.positionY + otherHSlots && newY + dragOriginalHeightSlots > other.positionY) {
                    hasCollision = true;
                    break;
                }
            }

            if (!hasCollision) {
                const shelfEl = document.querySelector(`.shelf[data-id="${draggedShelfId}"]`);
                if (shelfEl) {
                    shelfEl.style.left = `${newX * SLOT_SIZE_PX}px`;
                    shelfEl.style.top = `${newY * SLOT_SIZE_PX}px`;
                }
                shelf.tempX = newX;
                shelf.tempY = newY;
            }
            return;
        }

        // Если панорамируем поле
        if (isPanning) {
            camera.x = panCameraStartX + (e.clientX - panStartX);
            camera.y = panCameraStartY + (e.clientY - panStartY);
            updateTransform();
        }
    };

    // Обработчик окончания перетаскивания
    const onMouseUp = () => {
        // Сохраняем позицию полки
        if (isDraggingShelf && draggedShelfId !== null && isEditMode && isOwner) {
            const shelf = shelvesData.find(s => s.id === draggedShelfId);
            const shelfEl = document.querySelector(`.shelf[data-id="${draggedShelfId}"]`);
            if (shelf && shelfEl) {
                if (shelf.tempX !== undefined) {
                    shelf.positionX = shelf.tempX;
                    shelf.positionY = shelf.tempY;
                    delete shelf.tempX;
                    delete shelf.tempY;
                    drawShelves();
                }
                shelfEl.style.opacity = '';
                shelfEl.style.zIndex = '';
            }
        }
        isDraggingShelf = false;
        draggedShelfId = null;

        // Завершаем панорамирование
        isPanning = false;
        if (viewport) viewport.style.cursor = 'grab';
    };

    // Регистрируем обработчики
    if (viewport) {
        viewport.addEventListener('mousedown', onMouseDown);
        window.addEventListener('mousemove', onMouseMove);
        window.addEventListener('mouseup', onMouseUp);
    }
}


function startDrag(e, id, widthSlots, heightSlots) {
    e.preventDefault();
    e.stopPropagation();

    isDraggingShelf = true;
    draggedShelfId = id;
    dragStartX = e.clientX;
    dragStartY = e.clientY;

    const shelf = shelvesData.find(s => s.id === id);
    if (!shelf) return;

    dragOriginalSlotX = shelf.positionX;
    dragOriginalSlotY = shelf.positionY;
    dragOriginalWidthSlots = widthSlots;
    dragOriginalHeightSlots = heightSlots;

    const shelfEl = document.querySelector(`.shelf[data-id="${id}"]`);
    if (shelfEl) {
        shelfEl.style.opacity = '0.6';
        shelfEl.style.zIndex = '100';
    }
}

// --- ЗУМ И ПАНОРАМИРОВАНИЕ (Инициализация) ---

function initZoomAndPan() {
    const viewport = document.getElementById('roomViewport');
    const zoomIn = document.getElementById('zoomInBtn');
    const zoomOut = document.getElementById('zoomOutBtn');
    const reset = document.getElementById('resetViewBtn');

    camera.scale = DEFAULT_ZOOM;
    updateTransform();

    if (zoomIn) {
        zoomIn.addEventListener('click', () => {
            camera.scale = Math.min(MAX_ZOOM, camera.scale + 0.1);
            updateTransform();
            updateZoomLevel();
        });
    }
    if (zoomOut) {
        zoomOut.addEventListener('click', () => {
            camera.scale = Math.max(MIN_ZOOM, camera.scale - 0.1);
            updateTransform();
            updateZoomLevel();
        });
    }
    if (reset) {
        reset.addEventListener('click', () => {
            camera = { x: 0, y: 0, scale: DEFAULT_ZOOM };
            updateTransform();
            updateZoomLevel();
        });
    }

    if (viewport) {
        viewport.addEventListener('wheel', (e) => {
            e.preventDefault();
            const delta = e.deltaY > 0 ? -0.05 : 0.05;
            camera.scale = Math.min(MAX_ZOOM, Math.max(MIN_ZOOM, camera.scale + delta));
            updateTransform();
            updateZoomLevel();
        });
    }
}

function updateZoomLevel() {
    const zoomSpan = document.getElementById('zoomLevel');
    if (zoomSpan) {
        const percent = Math.round((camera.scale / DEFAULT_ZOOM) * 100);
        zoomSpan.textContent = `${percent}%`;
    }
}

function updateTransform() {
    const canvas = document.getElementById('roomCanvas');
    if (canvas) {
        canvas.style.transform = `translate(${camera.x}px, ${camera.y}px) scale(${camera.scale})`;
    }
}

function initAddShelfModal() {
    const openBtn = document.getElementById('openAddShelfModalBtn');
    const confirmBtn = document.getElementById('confirmAddShelfBtn');
    const widthInput = document.getElementById('shelfWidth');
    const heightInput = document.getElementById('shelfHeight');
    const capacitySpan = document.getElementById('shelfCapacity');  // span для отображения

    if (!openBtn || !confirmBtn) return;

    function updateCapacity() {
        const width = parseInt(widthInput.value) || 1;
        const maxCapacity = width * MAX_BOOKS_PER_UNIT;  // MAX_BOOKS_PER_UNIT = 15

        if (capacitySpan) {
            capacitySpan.textContent = maxCapacity;
        }
    }

    if (widthInput) {
        widthInput.addEventListener('input', updateCapacity);
        widthInput.addEventListener('change', updateCapacity);
    }

    openBtn.addEventListener('click', () => {
        widthInput.value = 2;
        heightInput.value = 2;
        updateCapacity();
        new bootstrap.Modal(document.getElementById('addShelfModal')).show();
    });

    confirmBtn.addEventListener('click', () => {
        const wBlocks = parseInt(widthInput.value);
        const hBlocks = parseInt(heightInput.value);

        if (isNaN(wBlocks) || isNaN(hBlocks) || wBlocks < 1 || hBlocks < 1) {
            showNotification('Введите корректные размеры (1-10)', 'error');
            return;
        }

        const width = wBlocks * SHELF_UNIT_SIZE;
        const height = hBlocks * SHELF_UNIT_SIZE;
        // Берём capacity из span (как число)
        let capacity = parseInt(capacitySpan.textContent);
        if (isNaN(capacity) || capacity < 1) {
            capacity = wBlocks * MAX_BOOKS_PER_UNIT;
        }

        if (addShelf(width, height, capacity)) {
            bootstrap.Modal.getInstance(document.getElementById('addShelfModal'))?.hide();
        }
    });
}

function openResizeShelfModal(shelfId) {
    const shelf = shelvesData.find(s => s.id === shelfId);
    if (!shelf) return;

    const widthInput = document.getElementById('resizeShelfWidth');
    const heightInput = document.getElementById('resizeShelfHeight');
    const capacitySpan = document.getElementById('resizeShelfCapacity');

    const widthBlocks = shelf.width / SHELF_UNIT_SIZE;
    const heightBlocks = shelf.height / SHELF_UNIT_SIZE;

    if (widthInput) widthInput.value = widthBlocks;
    if (heightInput) heightInput.value = heightBlocks;

    function updateResizeCapacity() {
        const width = parseInt(widthInput.value) || 1;
        const maxCapacity = width * MAX_BOOKS_PER_UNIT;

        if (capacitySpan) {
            capacitySpan.textContent = maxCapacity;
        }
    }

    if (widthInput) {
        widthInput.removeEventListener('input', updateResizeCapacity);
        widthInput.addEventListener('input', updateResizeCapacity);
    }

    if (capacitySpan) {
        updateResizeCapacity();
    }

    const modal = new bootstrap.Modal(document.getElementById('resizeShelfModal'));
    modal.show();

    document.getElementById('confirmResizeShelfBtn').onclick = () => {
        const newWBlocks = parseInt(widthInput.value);
        const newHBlocks = parseInt(heightInput.value);

        if (isNaN(newWBlocks) || isNaN(newHBlocks) || newWBlocks < 1 || newHBlocks < 1) {
            showNotification('Введите корректные размеры (1-10)', 'error');
            return;
        }

        const newWidth = newWBlocks * SHELF_UNIT_SIZE;
        const newHeight = newHBlocks * SHELF_UNIT_SIZE;
        let newCapacity = parseInt(capacitySpan.textContent);
        if (isNaN(newCapacity) || newCapacity < 1) {
            newCapacity = newWBlocks * MAX_BOOKS_PER_UNIT;
        }

        const books = shelf.books || [];
        const currentCount = books.length;

        if (currentCount > newCapacity) {
            showNotification('Текущее количество книг на полке больше заданной вместимости', 'error');
            return;
        }

        resizeShelf(shelfId, newWidth, newHeight, newCapacity);
        modal.hide();
    };
}


async function loadShelfBooks(shelfId) {
    try {
        const res = await fetch(`/libraries/${libraryId}/shelves/${shelfId}`);
        const result = await res.json();

        const ownerBlock = document.getElementById('ownerControlsBlock');
        const viewerBlock = document.getElementById('viewerBooksBlock');
        const ownerList = document.getElementById('shelfBooksList');
        const viewerList = document.getElementById('shelfBooksListViewer');
        const shelfInfoSpan = document.getElementById('shelfInfo');
        const addBooksBtn = document.getElementById('openAddBooksToShelfBtn');


        if (!result.success) return;

        // Находим полку в shelvesData
        const shelf = shelvesData.find(s => s.id === shelfId);
        const books = result.books || [];
        const currentCount = books.length;
        const capacity = shelf ? shelf.capacity : 0;
        const availableSpace = capacity - currentCount;

        if (addBooksBtn) {
                    addBooksBtn.disabled = (availableSpace <= 0);
                    addBooksBtn.title = availableSpace <= 0 ? 'Нет свободного места на полке' : 'Добавить книги';
                }

        // Обновляем информацию в спане
        if (shelfInfoSpan) {
            shelfInfoSpan.innerHTML = `
                <div class="d-flex justify-content-between mb-2">
                    <span>Книг на полке:</span>
                    <span class="fw-bold">${currentCount} / ${capacity}</span>
                </div>
                <div class="d-flex justify-content-between">
                    <span>Свободно места:</span>
                    <span class="fw-bold ${availableSpace === 0 ? 'text-danger' : 'text-success'}">${availableSpace}</span>
                </div>
            `;
        }

        const booksHtml = books.map(book => `
            <div class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    <input type="checkbox" class="form-check-input me-2 shelf-book-checkbox"
                           data-shelfbook-id="${book.shelfBookId}" ${!isOwner ? 'disabled' : ''}>
                    <a href="/books/book/${book.bookMarkId}" target="_blank">${escapeHtml(book.title)}</a>
                    <div class="small text-muted">${escapeHtml(book.author || '')}</div>
                </div>
            </div>
        `).join('');

        if (isOwner) {
            ownerBlock.classList.remove('d-none');
            viewerBlock.classList.add('d-none');
            ownerList.innerHTML = booksHtml || '<div class="text-muted text-center p-3">Полка пуста</div>';

            const removeSelectedBtn = document.getElementById('removeSelectedBooksBtn');
            if (removeSelectedBtn) {
                const newBtn = removeSelectedBtn.cloneNode(true);
                removeSelectedBtn.parentNode.replaceChild(newBtn, removeSelectedBtn);

                newBtn.onclick = async () => {
                    const selected = Array.from(document.querySelectorAll('.shelf-book-checkbox:checked'))
                        .map(cb => cb.dataset.shelfbookId);

                    if (selected.length) {

                        try {
                            for (const id of selected) {
                                await fetch(`/libraries/${libraryId}/shelfBook/${id}`, { method: 'DELETE' });
                            }

                            showNotification('Книги удалены', 'success');

                            // Закрываем модалку
                            const modal = bootstrap.Modal.getInstance(document.getElementById('editShelfBooksModal'));
                            if (modal) modal.hide();

                            // Обновляем комнату
                            await loadRoom(currentRoomId, document.getElementById('roomNameHeader')?.innerText);

                            // Открываем модалку заново с обновлёнными данными
                            setTimeout(() => {
                                loadShelfBooks(currentShelfId);
                            }, 300);

                        } catch (error) {
                            console.error('Error:', error);
                            showNotification('Ошибка при удалении', 'error');
                            newBtn.disabled = false;
                            newBtn.innerHTML = 'Удалить выбранные';
                        }
                    } else {
                        showNotification('Выберите книги', 'warning');
                    }
                };
            }
        } else {
            ownerBlock.classList.add('d-none');
            viewerBlock.classList.remove('d-none');
            viewerList.innerHTML = booksHtml || '<div class="text-muted text-center p-3">Полка пуста</div>';
        }

        new bootstrap.Modal(document.getElementById('editShelfBooksModal')).show();

    } catch(e) {
    e.printStackTrace();
        console.error(e);
        showNotification('Ошибка загрузки книг', 'error');
    }
}

function initShelvesModals() {
     const openModalBtn = document.getElementById('openAddBooksToShelfBtn');
     if (openModalBtn) {
         openModalBtn.addEventListener('click', () => {
             const editShelfModal = document.getElementById('editShelfBooksModal');
             if (editShelfModal) {
                 const modalInstance = bootstrap.Modal.getInstance(editShelfModal);
                 if (modalInstance) modalInstance.hide();
             }

             setTimeout(() => {
                 document.getElementById('userBooksList').innerHTML = '<div class="text-center">Загрузка...</div>';
                 loadUserBooks();
                 new bootstrap.Modal(document.getElementById('addBooksToShelfModal')).show();
             }, 300);
         });
     }

     const searchInput = document.getElementById('searchUserBooks');
     if (searchInput) {
         searchInput.addEventListener('input', debounce(() => loadUserBooks(searchInput.value), 300));
     }

     const confirmAddBtn = document.getElementById('confirmAddBooksBtn');
     if (confirmAddBtn) {
         confirmAddBtn.addEventListener('click', async () => {
             const selected = Array.from(document.querySelectorAll('.add-book-checkbox:checked')).map(cb => cb.value);

             if (!selected.length) {
                 showNotification('Выберите книги для добавления', 'warning');
                 return;
             }

             if (!currentShelfId) {
                 showNotification('Ошибка: полка не выбрана', 'error');
                 return;
             }

             confirmAddBtn.disabled = true;
             const originalText = confirmAddBtn.innerHTML;
             confirmAddBtn.innerHTML = 'Добавление...';

             try {
                 const response = await fetch(`/libraries/${libraryId}/shelves/${currentShelfId}/addBooks`, {
                     method: 'POST',
                     headers: { 'Content-Type': 'application/json' },
                     body: JSON.stringify({ bookMarkIds: selected })
                 });

                 const result = await response.json();

                 if (result.success) {
                     showNotification(result.message || 'Книги добавлены', 'success');

                     // Закрываем оба модальных окна в правильном порядке
                     const addBooksModal = bootstrap.Modal.getInstance(document.getElementById('addBooksToShelfModal'));
                     if (addBooksModal) addBooksModal.hide();

                     // Ждём закрытия первого модального окна, затем обновляем данные и переоткрываем окно полки
                     setTimeout(() => {
                         loadShelfBooks(currentShelfId);
                         loadRoom(currentRoomId, document.getElementById('roomNameHeader')?.innerText);
                     }, 200);

                 } else {
                     showNotification(result.message || 'Ошибка при добавлении', 'error');
                 }
             } catch (error) {
                 console.error('Error:', error);
                 showNotification('Ошибка соединения с сервером', 'error');
             } finally {
                 confirmAddBtn.disabled = false;
                 confirmAddBtn.innerHTML = originalText;
             }
         });
     }
 }

async function loadUserBooks(searchTerm = '') {
    try {
        const res = await fetch(`/libraries/${libraryId}/books?search=${encodeURIComponent(searchTerm)}`);
        const result = await res.json();
        if (result.success) {
            userBooks = result.books;
            const container = document.getElementById('userBooksList');
            container.innerHTML = userBooks.map(book => `
                <div class="list-group-item">
                    <input type="checkbox" class="form-check-input me-2 add-book-checkbox" value="${book.bookMarkId}">
                    <strong>${escapeHtml(book.title)}</strong> - ${escapeHtml(book.author)} (${book.isbn || ''})
                </div>
            `).join('');
        }
    } catch(e) {
        showNotification('Ошибка поиска книг', 'error');
    }
}

function initRoomActionButtons() {
    document.querySelectorAll('.edit-room-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const item = btn.closest('.room-item');
            document.getElementById('editRoomId').value = item.querySelector('.room-id')?.value;
            document.getElementById('editRoomName').value = item.querySelector('.room-name')?.value;
            new bootstrap.Modal(document.getElementById('editRoomModal')).show();
        });
    });

        document.querySelectorAll('.delete-room-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const item = btn.closest('.room-item');
                const roomId = item.querySelector('.room-id')?.value;
                const roomName = item.querySelector('.room-name')?.value;

                document.getElementById('deleteRoomId').value = roomId;
                document.getElementById('deleteRoomName').textContent = roomName;

                new bootstrap.Modal(document.getElementById('deleteRoomModal')).show();
            });
        });

    document.getElementById('saveRoomBtn')?.addEventListener('click', async () => {
        const roomId = document.getElementById('editRoomId').value;
        const newName = document.getElementById('editRoomName').value.trim();
        if (!newName) {
            document.getElementById('edit-room-error').classList.remove('d-none');
            return;
        }
        try {
            const res = await fetch(`/libraries/${libraryId}/rooms/${roomId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name: newName })
            });
            const result = await res.json();
            if (result.success) {
                showNotification('Комната обновлена');
                window.location.reload();
            } else {
                showNotification(result.message, 'error');
            }
        } catch (error) {
            showNotification('Ошибка соединения', 'error');
        }
    });

    document.getElementById('confirmDeleteRoomBtn')?.addEventListener('click', async () => {
        const roomId = document.getElementById('deleteRoomId').value;
        try {
            const res = await fetch(`/libraries/${libraryId}/rooms/${roomId}`, { method: 'DELETE' });
            const result = await res.json();
            if (result.success) {
                showNotification('Комната удалена');
                window.location.reload()
            } else {
                showNotification(result.message, 'error');
            }
        } catch (error) {
            showNotification('Ошибка соединения', 'error');
        }
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, m => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;' }[m]));
}

function showBookPopup(book, x, y) {
    let popup = document.getElementById('bookPopup');
    if (!popup) {
        popup = document.createElement('div');
        popup.id = 'bookPopup';

        document.body.appendChild(popup);
    }
    if (book.bookMarkId) {
            actionButtonHtml = `<a href="/books/book/${book.bookMarkId}" class="btn btn-sm btn-outline-success mt-2 w-100">Перейти к книге</a>`;
        } else {
            actionButtonHtml = `<button onclick="addFromLibrary(${book.bookDescriptionId}, ${libraryId})" class="btn btn-sm btn-outline-success mt-2 w-100">
                Добавить в мою библиотеку
            </button>`;
        }

    popup.innerHTML = `
        <img src="${book.cover || '/images/no-cover.png'}" style="width:100%; max-height:150px; object-fit:contain;">
        <div class="fw-bold mt-2">${escapeHtml(book.title)}</div>
        <div class="text-muted small">${escapeHtml(book.author || '')}</div>
         ${actionButtonHtml}

    `;
    popup.style.left = `${x + 10}px`;
    popup.style.top = `${y + 10}px`;
    popup.style.display = 'block';
    setTimeout(() => {
        document.addEventListener('click', function closePopup(e) {
            if (!popup.contains(e.target)) {
                popup.style.display = 'none';
                document.removeEventListener('click', closePopup);
            }
        });
    }, 0);
}

function addFromLibrary(bookDescriptionId, libraryId) {
    window.location.href = `/books/addFromLibrary?bookDescriptionId=${bookDescriptionId}&libraryId=${libraryId}`;
}

function debounce(fn, delay) {
    let timer;
    return function(...args) {
        clearTimeout(timer);
        timer = setTimeout(() => fn.apply(this, args), delay);
    };
}

// --- РАСЧЁТ ШИРИНЫ КНИГИ ---
function calculateBookWidth(shelfWidthPx, bookCount, gap, minWidth) {
    if (bookCount === 0) return minWidth;
    const availableWidth = shelfWidthPx - 8; // вычитаем padding 4px слева и справа
    const totalGapWidth = (bookCount - 1) * gap;
    const calculatedWidth = (availableWidth - totalGapWidth) / bookCount;
    return Math.max(minWidth, Math.floor(calculatedWidth));
}

// Инициализация поиска
function initSearch() {
    const searchBtn = document.getElementById('searchBookBtn');
    const searchInput = document.getElementById('searchBookInput');
    const clearBtn = document.getElementById('clearSearchBtn');

    if (searchBtn) {
        searchBtn.addEventListener('click', performSearch);
    }
    if (searchInput) {
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') performSearch();
        });
    }
    if (clearBtn) {
        clearBtn.addEventListener('click', clearSearch);
    }
}

// Выполнение поиска
async function performSearch() {
    const searchTerm = document.getElementById('searchBookInput')?.value.trim();
    if (!searchTerm) {
        clearSearch();
        return;
    }

//    const resultsDiv = document.getElementById('searchResults');
//    const resultsMessage = document.getElementById('searchResultsMessage');
//    const foundBooksList = document.getElementById('foundBooksList');
    const clearBtn = document.getElementById('clearSearchBtn');

//    resultsDiv.style.display = 'block';
//    foundBooksList.innerHTML = '';

    try {
        const response = await fetch(`/libraries/${libraryId}/books/search?query=${encodeURIComponent(searchTerm)}`);
        const result = await response.json();

        if (result.success && result.books.length > 0) {
            currentSearchResults = result.books;
            isSearchActive = true;

            const roomsFound = [...new Set(result.books.map(b => b.roomName))];
//            resultsMessage.innerHTML = `
//                Найдено книг: ${result.books.length}<br>
//                <small>В комнатах: ${roomsFound.join(', ')}</small>
//            `;

            clearBtn.style.display = 'block';

            // Подсвечиваем найденные книги (убираем старую подсветку и ставим новую)
            clearHighlights();
            highlightFoundBooks(currentSearchResults);

//            showNotification(`Найдено ${result.books.length} книг в ${roomsFound.length} комнатах`, 'info');

        } else {
//            resultsMessage.innerHTML = 'Ничего не найдено';
//            foundBooksList.innerHTML = '';
//            clearSearch();
        }

    } catch (error) {
        console.error('Search error:', error);
//        resultsMessage.innerHTML = 'Ошибка поиска';
        showNotification('Ошибка при поиске', 'error');
    }
}

// Подсветка найденных книг на полках
function highlightFoundBooks(books) {

    if (!books || books.length === 0) return;

    // Подсвечиваем КАЖДУЮ полку для КАЖДОЙ найденной книги
    books.forEach(book => {


        const shelfId = book.shelfId;

        if (shelfId) {
            const shelfEl = document.querySelector(`.shelf[data-id="${shelfId}"]`);
            if (shelfEl) {
                shelfEl.classList.add('highlight-shelf');

                const shelfBookId = book.shelfBookId;
                if (shelfBookId) {
                    const bookEl = shelfEl.querySelector(`.book-on-shelf[data-shelf-book-id="${shelfBookId}"]`);
                    if (bookEl) {
                        bookEl.classList.add('highlight');
                    } else {
                        console.log('Book not found with shelfBookId:', shelfBookId);
                    }
                }

            } else {
                console.log('Shelf not found:', shelfId);
            }
        }
    });
}

// Снятие подсветки
function clearHighlights() {
    document.querySelectorAll('.shelf.highlight-shelf').forEach(el => {
        el.classList.remove('highlight-shelf');
    });
    document.querySelectorAll('.book-on-shelf.highlight').forEach(el => {
            el.classList.remove('highlight');
        });
}

// Очистка поиска
function clearSearch() {
//    const resultsDiv = document.getElementById('searchResults');
    const searchInput = document.getElementById('searchBookInput');
    const clearBtn = document.getElementById('clearSearchBtn');

//    resultsDiv.style.display = 'none';
//    document.getElementById('searchResultsMessage').innerHTML = '';
//    document.getElementById('foundBooksList').innerHTML = '';
    clearBtn.style.display = 'none';

    currentSearchResults = [];
    isSearchActive = false;
    clearHighlights();  // Убираем подсветку при очистке

    if (searchInput) searchInput.value = '';
}

function initModalEnterSubmit() {
    // Находим все модалки
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
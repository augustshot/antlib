window.addEventListener("load", init);

function init() {

 deleteBtn = document.getElementById('deleteBookBtn');
    if (deleteBtn) {
        deleteBtn.addEventListener('click', function() {
            const bookTitle = this.getAttribute('data-book-title');
            document.getElementById('deleteBookTitle').textContent = bookTitle || 'эту книгу';
            
            const deleteModal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
            deleteModal.show();
        });
    }
    }
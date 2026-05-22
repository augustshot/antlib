window.addEventListener("load", init);

function init() {
    const cancelBtn = document.getElementById('cancelBtn');
    const confirmExitBtn = document.getElementById('confirmExitBtn');

    const form = document.querySelector('form[th\\:object="${userBook}"]') || 
                 document.querySelector('form');
    
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const url = this.getAttribute('href');
            
            if (isAnyFieldFilled(form)) {
                const modal = new bootstrap.Modal(document.getElementById('confirmExitModal'));
                modal.show();
                window.pendingUrl = url;
            } else {
                window.location.href = url;
            }
        });
    }

    if (confirmExitBtn) {
        confirmExitBtn.addEventListener('click', function() {
            if (window.pendingUrl) {
                window.location.href = window.pendingUrl;
            }
        });
    }
}

function isAnyFieldFilled(form) {
    if (!form) return false;
    
    const inputs = form.querySelectorAll('input, textarea, select');
    
    for (let input of inputs) {
        if (input.type === 'button' || input.type === 'submit' || input.type === 'hidden') continue;
        if (input.disabled) continue;

        if (input.tagName === 'SELECT') {
            if (input.value && input.value !== '') return true;
        }
        else if (input.value && input.value.trim() !== '') {
            return true;
        }
    }
    return false;
}
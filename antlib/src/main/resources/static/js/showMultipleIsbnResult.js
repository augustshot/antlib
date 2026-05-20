window.addEventListener('DOMContentLoaded', function() {
        var flagInput = document.getElementById('showResultFlag');
        if (flagInput && flagInput.value === 'true') {
            var modalEl = document.getElementById('resultModal');
            var modal = new bootstrap.Modal(modalEl);
            modal.show();
        }
    });
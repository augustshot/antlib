function debounce(func, delay) {
        let timeout;
        return function(...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    function submitFilterForm() {
        document.getElementById('filterForm').submit();
    }

    function initFilters() {
        const textFields = ['title', 'author', 'language', 'isbn'];
        const debouncedSubmit = debounce(submitFilterForm, 2000);

        textFields.forEach(fieldName => {
            const element = document.querySelector(`[name="${fieldName}"]`);
            if (element) {
                element.addEventListener('input', debouncedSubmit);
            }
        });

        const selectFields = ['ratingFrom', 'ratingTo', 'status', 'source'];
        selectFields.forEach(fieldName => {
            const element = document.querySelector(`[name="${fieldName}"]`);
            if (element) {
                element.addEventListener('change', submitFilterForm);
            }
        });
    }

    document.addEventListener('DOMContentLoaded', initFilters);
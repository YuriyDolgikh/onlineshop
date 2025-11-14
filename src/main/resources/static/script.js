// Проверка статуса API
async function checkApiStatus() {
    const statusElement = document.getElementById('status');

    try {
        const response = await fetch('/v3/api-docs', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.ok) {
            statusElement.innerHTML = '✅ API работает корректно';
            statusElement.style.background = '#e8f5e8';
            statusElement.style.color = '#2d5016';
        } else {
            throw new Error('API не отвечает');
        }
    } catch (error) {
        statusElement.innerHTML = '❌ Ошибка подключения к API';
        statusElement.style.background = '#ffebee';
        statusElement.style.color = '#c62828';
        console.error('API status check failed:', error);
    }
}

// Загрузка модального окна
async function loadModal(modalFile) {
    try {
        const response = await fetch(modalFile);
        const modalContent = await response.text();

        const modalContainer = document.getElementById('modalContainer');
        modalContainer.innerHTML = `
            <div class="modal" id="dynamicModal">
                ${modalContent}
            </div>
        `;

        const modal = document.getElementById('dynamicModal');
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';

        // Добавляем обработчики событий для нового модального окна
        setupModalEvents();

    } catch (error) {
        console.error('Error loading modal:', error);
    }
}

// Закрытие модального окна
function closeModal() {
    const modal = document.getElementById('dynamicModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

// Настройка обработчиков событий для модального окна
function setupModalEvents() {
    const modal = document.getElementById('dynamicModal');

    // Закрытие при клике вне модального окна
    if (modal) {
        modal.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    }

    // Закрытие по ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeModal();
        }
    });
}

// Проверяем статус при загрузке страницы
document.addEventListener('DOMContentLoaded', checkApiStatus);
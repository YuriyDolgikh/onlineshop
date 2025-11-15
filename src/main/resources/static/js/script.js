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

// Загрузка модального окна с поддержкой языка
async function loadModal(modalFile) {
    try {
        const lang = localStorage.getItem('preferred-language') || 'ru';
        let modalPath;

        // Если выбран английский язык, пробуем загрузить английскую версию
        if (lang === 'en') {
            modalPath = modalFile.replace('.html', '-en.html');
        } else {
            modalPath = modalFile;
        }

        const response = await fetch(modalPath);

        if (!response.ok) {
            // Если файл на выбранном языке не найден, загружаем русскую версию
            const fallbackResponse = await fetch(modalFile);
            if (!fallbackResponse.ok) {
                throw new Error(`HTTP error! status: ${fallbackResponse.status}`);
            }
            const fallbackContent = await fallbackResponse.text();
            showModal(fallbackContent);
            return;
        }

        const modalContent = await response.text();
        showModal(modalContent);

    } catch (error) {
        console.error('Error loading modal:', error);
        alert('Ошибка загрузки информации. Пожалуйста, попробуйте позже.');
    }
}

// Показать модальное окно
function showModal(modalContent) {
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

// Проверка доступности ресурсов
async function checkResources() {
    try {
        // Проверка CSS
        const cssResponse = await fetch('/css/styles.css');
        if (!cssResponse.ok) console.warn('CSS not loaded from expected path');

        // Проверка JS
        const jsResponse = await fetch('/js/script.js');
        if (!jsResponse.ok) console.warn('JS not loaded from expected path');

        console.log('Resource check completed');
    } catch (error) {
        console.warn('Resource loading warning:', error);
    }
}

// Проверяем статус при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    checkApiStatus();
    checkResources();
});

// Добавляем глобальную функцию для обработки ошибок
window.addEventListener('error', function(event) {
    console.error('Global error:', event.error);
});
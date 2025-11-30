// Проверка статуса API
async function checkApiStatus() {
    const statusElement = document.getElementById('status');
    const lang = localStorage.getItem('preferred-language') || 'ru';

    try {
        const response = await fetch('/v3/api-docs', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.ok) {
            setApiStatus('success', lang);
        } else {
            throw new Error('API не отвечает');
        }
    } catch (error) {
        setApiStatus('error', lang);
        console.error('API status check failed:', error);
    }
}

// Установка статуса API с учетом языка
function setApiStatus(status, lang) {
    const statusElement = document.getElementById('status');
    const statusMessages = {
        en: {
            success: '✅ API is working correctly',
            error: '❌ API connection error'
        },
        de: {
            success: '✅ API funktioniert korrekt',
            error: '❌ API-Verbindungsfehler'
        },
        ru: {
            success: '✅ API работает корректно',
            error: '❌ Ошибка подключения к API'
        }
    };

    // Сохраняем текущее состояние в data-атрибут
    statusElement.dataset.apiStatus = status;

    if (status === 'success') {
        statusElement.textContent = statusMessages[lang].success;
        statusElement.style.background = '#e8f5e8';
        statusElement.style.color = '#2d5016';
    } else {
        statusElement.textContent = statusMessages[lang].error;
        statusElement.style.background = '#ffebee';
        statusElement.style.color = '#c62828';
    }
}

// Загрузка модального окна с поддержкой языка
async function loadModal(modalFile) {
    try {
        const lang = localStorage.getItem('preferred-language') || 'ru';
        let modalPath;

        // Определяем путь к файлу в зависимости от языка
        if (lang === 'en') {
            modalPath = modalFile.replace('.html', '-en.html');
        } else if (lang === 'de') {
            modalPath = modalFile.replace('.html', '-de.html');
        } else {
            modalPath = modalFile; // русский по умолчанию
        }

        console.log(`Loading modal: ${modalPath} for language: ${lang}`);

        const response = await fetch(modalPath);

        if (!response.ok) {
            // Если файл на выбранном языке не найден, пробуем русскую версию
            console.warn(`Modal file not found: ${modalPath}, trying fallback`);

            if (lang !== 'ru') {
                const fallbackResponse = await fetch(modalFile);
                if (!fallbackResponse.ok) {
                    throw new Error(`HTTP error! status: ${fallbackResponse.status}`);
                }
                const fallbackContent = await fallbackResponse.text();
                showModal(fallbackContent);
                return;
            } else {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        }

        const modalContent = await response.text();
        showModal(modalContent);

    } catch (error) {
        console.error('Error loading modal:', error);
        // Показываем сообщение на текущем языке
        const lang = localStorage.getItem('preferred-language') || 'ru';
        const errorMessage = lang === 'en'
            ? 'Error loading information. Please try again later.'
            : lang === 'de'
                ? 'Fehler beim Laden der Informationen. Bitte versuchen Sie es später erneut.'
                : 'Ошибка загрузки информации. Пожалуйста, попробуйте позже.';
        alert(errorMessage);
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

    // Добавляем обработчик для кнопки закрытия внутри модального окна
    const closeBtn = document.querySelector('.modal-close-btn');
    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    }
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
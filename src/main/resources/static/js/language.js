// Тексты для перевода
const translations = {
    en: {
        'main-title': 'OnlineShop API',
        'subtitle': 'Full REST API for managing an online store',
        'status': '🔄 Checking API status...',
        'swagger-btn': '📚 Open Swagger Documentation',
        'base-url-label': 'Base URL:',
        'api-version-label': 'API Version:',
        'sections-title': '📋 API Sections',
        'users-title': 'User Management',
        'users-desc': 'Registration, authentication, profile and role management',
        'products-title': 'Product Catalog',
        'products-desc': 'Complete product management: creation, editing, search and filtering',
        'carts-title': 'Shopping Cart',
        'carts-desc': 'Cart management: adding, removing items, changing quantities',
        'cartitems-title': 'Cart Items',
        'cartitems-desc': 'Detailed management of cart positions and their quantities',
        'favorites-title': 'Favorites',
        'favorites-desc': 'Management of favorite products for users',
        'categories-title': 'Product Categories',
        'categories-desc': 'Management of categories and subcategories for catalog organization',
        'orders-title': 'Order Management',
        'orders-desc': 'Order creation, retrieval, cancellation and payment confirmation',
        'orderitems-title': 'Order Items',
        'orderitems-desc': 'Management of items in orders: updating quantities, deletion',
        'statistics-title': 'Statistics and Analytics',
        'statistics-desc': 'Retrieval of statistical data and analytical reports for the store',
        'footer': '&copy; 2025 OnlineShop API.'
    },
    de: {
        'main-title': 'OnlineShop API',
        'subtitle': 'Vollständige REST API zur Verwaltung eines Online-Shops',
        'status': '🔄 API-Status wird überprüft...',
        'swagger-btn': '📚 Swagger Dokumentation öffnen',
        'base-url-label': 'Basis-URL:',
        'api-version-label': 'API-Version:',
        'sections-title': '📋 API-Bereiche',
        'users-title': 'Benutzerverwaltung',
        'users-desc': 'Registrierung, Authentifizierung, Profil- und Rollenverwaltung',
        'products-title': 'Produktkatalog',
        'products-desc': 'Vollständige Produktverwaltung: Erstellung, Bearbeitung, Suche und Filterung',
        'carts-title': 'Warenkorb',
        'carts-desc': 'Warenkorbverwaltung: Hinzufügen, Entfernen von Artikeln, Mengenänderung',
        'cartitems-title': 'Warenkorbpositionen',
        'cartitems-desc': 'Detaillierte Verwaltung von Warenkorbpositionen und deren Mengen',
        'favorites-title': 'Favoriten',
        'favorites-desc': 'Verwaltung von favorisierten Produkten für Benutzer',
        'categories-title': 'Produktkategorien',
        'categories-desc': 'Verwaltung von Kategorien und Unterkategorien für die Katalogorganisation',
        'orders-title': 'Bestellverwaltung',
        'orders-desc': 'Bestellerstellung, Abruf, Stornierung und Zahlungsbestätigung',
        'orderitems-title': 'Bestellpositionen',
        'orderitems-desc': 'Verwaltung von Artikeln in Bestellungen: Mengenaktualisierung, Löschung',
        'statistics-title': 'Statistiken und Analysen',
        'statistics-desc': 'Abruf von statistischen Daten und analytischen Berichten für den Shop',
        'footer': '&copy; 2025 OnlineShop API.'
    },
    ru: {
        'main-title': 'OnlineShop API',
        'subtitle': 'Full REST API для управления интернет-магазином',
        'status': '🔄 Проверка статуса API...',
        'swagger-btn': '📚 Открыть Swagger Documentation',
        'base-url-label': 'Base URL:',
        'api-version-label': 'API Version:',
        'sections-title': '📋 Разделы API',
        'users-title': 'Управление пользователями',
        'users-desc': 'Регистрация, аутентификация, управление профилями и ролями пользователей',
        'products-title': 'Каталог товаров',
        'products-desc': 'Полное управление продуктами: создание, редактирование, поиск и фильтрация',
        'carts-title': 'Корзина покупок',
        'carts-desc': 'Управление корзиной: добавление, удаление товаров, изменение количества',
        'cartitems-title': 'Элементы корзины',
        'cartitems-desc': 'Детальное управление позициями в корзине и их количеством',
        'favorites-title': 'Избранное',
        'favorites-desc': 'Управление списком избранных товаров для пользователей',
        'categories-title': 'Категории товаров',
        'categories-desc': 'Управление категориями и подкатегориями для организации каталога',
        'orders-title': 'Управление заказами',
        'orders-desc': 'Создание, получение, отмена заказов и подтверждение оплаты',
        'orderitems-title': 'Элементы заказов',
        'orderitems-desc': 'Управление товарами в заказах: обновление количества, удаление',
        'statistics-title': 'Статистика и аналитика',
        'statistics-desc': 'Получение статистических данных и аналитических отчетов по магазину',
        'footer': '&copy; 2025 OnlineShop API.'
    }
};

// Функция смены языка
function changeLanguage(lang) {
    // Обновляем активную кнопку
    document.querySelectorAll('.lang-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    // Обновляем тексты на странице
    Object.keys(translations[lang]).forEach(key => {
        const element = document.getElementById(key);
        if (element) {
            if (key === 'footer') {
                element.innerHTML = translations[lang][key];
            } else {
                element.textContent = translations[lang][key];
            }
        }
    });

    // Сохраняем выбор языка
    localStorage.setItem('preferred-language', lang);

    // Обновляем статус API с сохранением текущего состояния
    updateApiStatus(lang);

    // Обновляем модальные окна, если они открыты
    updateModalLanguage(lang);
}

// Функция обновления статуса API с сохранением состояния
function updateApiStatus(lang) {
    const statusElement = document.getElementById('status');
    const currentStatus = statusElement.dataset.apiStatus; // Получаем сохраненное состояние

    // Если состояние уже определено (success или error), обновляем текст
    if (currentStatus === 'success' || currentStatus === 'error') {
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

        if (currentStatus === 'success') {
            statusElement.textContent = statusMessages[lang].success;
            statusElement.style.background = '#e8f5e8';
            statusElement.style.color = '#2d5016';
        } else {
            statusElement.textContent = statusMessages[lang].error;
            statusElement.style.background = '#ffebee';
            statusElement.style.color = '#c62828';
        }
    }
    // Если состояние не определено, просто устанавливаем текст проверки
    else {
        const checkingMessages = {
            en: '🔄 Checking API status...',
            de: '🔄 API-Status wird überprüft...',
            ru: '🔄 Проверка статуса API...'
        };
        statusElement.textContent = checkingMessages[lang];
        statusElement.style.background = '';
        statusElement.style.color = '';
    }
}

// Функция обновления языка в модальных окнах
function updateModalLanguage(lang) {
    const modalContent = document.querySelector('.modal-content');
    if (!modalContent) return;

    // Получаем имя текущего модального окна
    const modalName = getCurrentModalName();
    if (!modalName) return;

    // Загружаем контент на нужном языке
    loadModalContent(modalName, lang);
}

// Функция для получения имени текущего модального окна
function getCurrentModalName() {
    const modal = document.querySelector('.modal-content');
    if (!modal) return null;

    const header = modal.querySelector('.modal-header h2');
    if (!header) return null;

    // Определяем имя модального окна по заголовку
    const title = header.textContent;

    // Русские заголовки
    if (title.includes('корзины') || title.includes('Корзина')) return 'cart-modal.html';
    if (title.includes('элементов корзины') || title.includes('Элементы корзины')) return 'cartitem-modal.html';
    if (title.includes('категорий') || title.includes('Категории')) return 'category-modal.html';
    if (title.includes('избранных') || title.includes('Избранное')) return 'favourite-modal.html';
    if (title.includes('заказами') || title.includes('Заказы')) return 'order-modal.html';
    if (title.includes('элементов заказов') || title.includes('Элементы заказов')) return 'orderitem-modal.html';
    if (title.includes('товарами') || title.includes('Товары')) return 'product-modal.html';
    if (title.includes('статистики') || title.includes('Статистика')) return 'statistic-modal.html';
    if (title.includes('пользователями') || title.includes('Пользователи')) return 'user-modal.html';

    // Английские заголовки
    if (title.includes('Cart') && !title.includes('Items')) return 'cart-modal.html';
    if (title.includes('Cart Items')) return 'cartitem-modal.html';
    if (title.includes('Categories')) return 'category-modal.html';
    if (title.includes('Favorites')) return 'favourite-modal.html';
    if (title.includes('Orders')) return 'order-modal.html';
    if (title.includes('Order Items')) return 'orderitem-modal.html';
    if (title.includes('Products')) return 'product-modal.html';
    if (title.includes('Statistics')) return 'statistic-modal.html';
    if (title.includes('Users')) return 'user-modal.html';

    // Немецкие заголовки
    if (title.includes('Warenkorb') && !title.includes('positionen')) return 'cart-modal.html';
    if (title.includes('Warenkorbpositionen')) return 'cartitem-modal.html';
    if (title.includes('Kategorien')) return 'category-modal.html';
    if (title.includes('Favoriten')) return 'favourite-modal.html';
    if (title.includes('Bestellungen')) return 'order-modal.html';
    if (title.includes('Bestellpositionen')) return 'orderitem-modal.html';
    if (title.includes('Produkte')) return 'product-modal.html';
    if (title.includes('Statistiken')) return 'statistic-modal.html';
    if (title.includes('Benutzer')) return 'user-modal.html';

    return null;
}

// Функция загрузки контента модального окна на нужном языке
function loadModalContent(modalName, lang) {
    // Перезагружаем модальное окно с новым языком
    loadModal(modalName);
}

// При загрузке страницы устанавливаем язык из localStorage
document.addEventListener('DOMContentLoaded', function() {
    const savedLang = localStorage.getItem('preferred-language') || 'en';

    // Активируем соответствующую кнопку
    const buttons = document.querySelectorAll('.lang-btn');
    buttons.forEach(btn => {
        btn.classList.remove('active');
        if ((savedLang === 'en' && btn.textContent === 'English') ||
            (savedLang === 'de' && btn.textContent === 'Deutsch') ||
            (savedLang === 'ru' && btn.textContent === 'Русский')) {
            btn.classList.add('active');
        }
    });

    // Применяем перевод
    Object.keys(translations[savedLang]).forEach(key => {
        const element = document.getElementById(key);
        if (element) {
            if (key === 'footer') {
                element.innerHTML = translations[savedLang][key];
            } else {
                element.textContent = translations[savedLang][key];
            }
        }
    });
});
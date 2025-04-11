const udid = "86c1c3528327635aa5bcf664d5dd4b51cc7af0a3";
let deviceScreenSize = null;
let sessionActive = true;
let refreshInterval = null;
let activityTimeout = null;
let swipeStart = null;
let homePressStart = null;

// Получение разрешения экрана устройства
async function fetchScreenSize() {
    try {
        const response = await fetch(`/api/screen_size/${udid}`);
        if (!response.ok) throw new Error("Failed to fetch screen size");
        deviceScreenSize = await response.json();
        console.log("Device screen size:", deviceScreenSize);
    } catch (error) {
        console.error("Error fetching screen size:", error);
    }
}

// Проверка статуса сессии
async function checkSessionStatus() {
    try {
        const response = await fetch(`/api/screenshot/${udid}?t=${new Date().getTime()}`);
        if (!response.ok) throw new Error("Session not active");
        sessionActive = true;
    } catch (error) {
        console.error("Session check failed:", error);
        sessionActive = false;
    }
}

// Установка интервала обновления
function setRefreshInterval(intervalMs) {
    if (refreshInterval) clearInterval(refreshInterval);
    refreshInterval = setInterval(() => {
        if (sessionActive) refreshScreenshot();
        else {
            clearInterval(refreshInterval);
            console.log("Session inactive, stopping screenshot refresh");
        }
    }, intervalMs);
}

// Запуск быстрого обновления после действия
function startActiveRefresh() {
    setRefreshInterval(700);
    if (activityTimeout) clearTimeout(activityTimeout);
    activityTimeout = setTimeout(() => {
        setRefreshInterval(10000);
        console.log("Switching to idle refresh (10s)");
    }, 30000);
}

// Инициализация сессии
async function initializeSession() {
    try {
        console.log("Initializing session for UDID:", udid);
        const response = await fetch('/api/start_session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Failed to start session");
        console.log("Session started:", data);
        await fetchScreenSize();
        sessionActive = true;
        refreshScreenshot();
        setRefreshInterval(10000); // Стартуем с 10 сек
    } catch (error) {
        console.error("Failed to initialize session:", error);
        sessionActive = false;
    }
}

// Обработка клика по изображению
async function tapOnImage(event) {
    if (!deviceScreenSize) {
        console.error("Device screen size not available");
        return;
    }

    const img = event.target;
    const rect = img.getBoundingClientRect();
    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;
    const imgWidth = rect.width;
    const imgHeight = rect.height;

    const deviceX = Math.round((clickX / imgWidth) * deviceScreenSize.width);
    const deviceY = Math.round((clickY / imgHeight) * deviceScreenSize.height);

    console.log(`Click at image (${clickX}, ${clickY}) -> Device (${deviceX}, ${deviceY})`);

    try {
        const response = await fetch('/api/tap', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid, x: deviceX, y: deviceY })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Tap failed");
        console.log("Tap successful:", data);
        startActiveRefresh();
    } catch (error) {
        console.error("Tap failed:", error);
        sessionActive = false;
    }
}

// Начало свайпа
function startSwipe(event) {
    if (!deviceScreenSize) {
        console.error("Device screen size not available");
        return;
    }

    const img = event.target;
    const rect = img.getBoundingClientRect();
    swipeStart = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
    };
    console.log(`Swipe started at image (${swipeStart.x}, ${swipeStart.y})`);
}

// Обновление свайпа (опционально, для визуализации)
function updateSwipe(event) {
    // Можно добавить визуализацию линии свайпа, если нужно
}

// Конец свайпа
async function endSwipe(event) {
    if (!swipeStart || !deviceScreenSize) {
        console.error("Swipe not started or device screen size not available");
        return;
    }

    const img = event.target;
    const rect = img.getBoundingClientRect();
    const endX = event.clientX - rect.left;
    const endY = event.clientY - rect.top;

    const imgWidth = rect.width;
    const imgHeight = rect.height;

    const deviceStartX = Math.round((swipeStart.x / imgWidth) * deviceScreenSize.width);
    const deviceStartY = Math.round((swipeStart.y / imgHeight) * deviceScreenSize.height);
    const deviceEndX = Math.round((endX / imgWidth) * deviceScreenSize.width);
    const deviceEndY = Math.round((endY / imgHeight) * deviceScreenSize.height);

    console.log(`Swipe from image (${swipeStart.x}, ${swipeStart.y}) to (${endX}, ${endY}) -> Device (${deviceStartX}, ${deviceStartY}) to (${deviceEndX}, ${deviceEndY})`);

    try {
        const response = await fetch('/api/swipe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                udid: udid,
                start_x: deviceStartX,
                start_y: deviceStartY,
                end_x: deviceEndX,
                end_y: deviceEndY
            })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Swipe failed");
        console.log("Swipe successful:", data);
        startActiveRefresh();
    } catch (error) {
        console.error("Swipe failed:", error);
        sessionActive = false;
    } finally {
        swipeStart = null;
    }
}

// Ввод текста
async function typeText() {
    const text = document.getElementById('textInput').value;
    if (!text) return;

    try {
        const response = await fetch('/api/type', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid, text: text })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Type text failed");
        console.log("Type text successful:", data);
        document.getElementById('textInput').value = ''; // Очищаем поле
        startActiveRefresh();
    } catch (error) {
        console.error("Type text failed:", error);
        sessionActive = false;
    }
}

// Начало нажатия на кнопку Home
function startHomePress(event) {
    homePressStart = Date.now();
}

// Конец нажатия на кнопку Home
async function endHomePress(event) {
    if (!homePressStart) return;

    const pressDuration = Date.now() - homePressStart;
    const isLongPress = pressDuration >= 1000; // Долгое нажатие >= 1 сек

    try {
        const endpoint = isLongPress ? '/api/long_press_home' : '/api/press_home';
        console.log(`Home button ${isLongPress ? 'long pressed' : 'pressed'} for ${pressDuration}ms`);

        const response = await fetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || `${isLongPress ? 'Long press' : 'Press'} home failed`);
        console.log(`Home button ${isLongPress ? 'long press' : 'press'} successful:`, data);
        startActiveRefresh();
    } catch (error) {
        console.error(`Home button ${isLongPress ? 'long press' : 'press'} failed:`, error);
        sessionActive = false;
    } finally {
        homePressStart = null;
    }
}

function refreshScreenshot() {
    const screen = document.getElementById('screen');
    const url = `/api/screenshot/${udid}?t=${new Date().getTime()}`;
    console.log("Requesting screenshot from:", url);
    screen.src = url;
    screen.onerror = async () => {
        console.error("Screenshot failed, checking session...");
        await checkSessionStatus();
    };
}

// Инициализируем сессию при загрузке страницы
initializeSession();
const udid = "86c1c3528327635aa5bcf664d5dd4b51cc7af0a3";
let deviceScreenSize = null;

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
        if (data.status !== "success") {
            throw new Error(data.message || "Failed to start session");
        }
        console.log("Session started:", data);
        await fetchScreenSize();
        refreshScreenshot();
        setInterval(refreshScreenshot, 1000);
    } catch (error) {
        console.error("Failed to initialize session:", error);
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

    // Координаты клика относительно изображения
    const clickX = event.clientX - rect.left;
    const clickY = event.clientY - rect.top;

    // Размеры изображения в интерфейсе
    const imgWidth = rect.width;
    const imgHeight = rect.height;

    // Конвертация координат в пространство устройства
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
        if (data.status !== "success") {
            throw new Error(data.message || "Tap failed");
        }
        console.log("Tap successful:", data);
        refreshScreenshot();
    } catch (error) {
        console.error("Tap failed:", error);
    }
}

async function swipe() {
    const startX = parseInt(document.getElementById('startX').value);
    const startY = parseInt(document.getElementById('startY').value);
    const endX = parseInt(document.getElementById('endX').value);
    const endY = parseInt(document.getElementById('endY').value);
    try {
        const response = await fetch('/api/swipe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid, start_x: startX, start_y: startY, end_x: endX, end_y: endY })
        });
        const data = await response.json();
        if (data.status !== "success") {
            throw new Error(data.message || "Swipe failed");
        }
        console.log("Swipe successful:", data);
        refreshScreenshot();
    } catch (error) {
        console.error("Swipe failed:", error);
    }
}

async function typeText() {
    const text = document.getElementById('textInput').value;
    try {
        const response = await fetch('/api/type', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid, text: text })
        });
        const data = await response.json();
        if (data.status !== "success") {
            throw new Error(data.message || "Type text failed");
        }
        console.log("Type text successful:", data);
        refreshScreenshot();
    } catch (error) {
        console.error("Type text failed:", error);
    }
}

async function stopSession() {
    try {
        console.log("Stopping session for UDID:", udid);
        const response = await fetch('/api/stop_session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid: udid })
        });
        const data = await response.json();
        if (data.status !== "success") {
            throw new Error(data.message || "Failed to stop session");
        }
        console.log("Session stopped:", data);
    } catch (error) {
        console.error("Failed to stop session:", error);
    }
}

function refreshScreenshot() {
    const screen = document.getElementById('screen');
    const url = `/api/screenshot/${udid}?t=${new Date().getTime()}`;
    console.log("Requesting screenshot from:", url);
    screen.src = url;
}

// Инициализируем сессию при загрузке страницы
initializeSession();
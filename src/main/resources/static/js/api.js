async function fetchScreenSize(udid) {
    try {
        const response = await fetch(`/api/screen_size/${udid}`);
        if (!response.ok) throw new Error("Failed to fetch screen size");
        return await response.json();
    } catch (error) {
        console.error("Error fetching screen size:", error);
        throw error;
    }
}

async function checkSessionStatus(udid) {
    try {
        const response = await fetch(`/api/screenshot/${udid}?t=${new Date().getTime()}`);
        if (!response.ok) throw new Error("Session not active");
        return true;
    } catch (error) {
        console.error("Session check failed:", error);
        return false;
    }
}

async function startSession(udid, deviceType) {
    try {
        const response = await fetch('/api/start_session', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid, deviceType })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Failed to start session");
        return data;
    } catch (error) {
        console.error("Failed to start session:", error);
        throw error;
    }
}

async function tap(udid, x, y, deviceType) {
    try {
        const response = await fetch('/api/tap', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid, x, y, deviceType })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Tap failed");
        return data;
    } catch (error) {
        console.error("Tap failed:", error);
        throw error;
    }
}

async function swipe(udid, start_x, start_y, end_x, end_y, deviceType) {
    try {
        const response = await fetch('/api/swipe', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid, start_x, start_y, end_x, end_y, deviceType })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Swipe failed");
        return data;
    } catch (error) {
        console.error("Swipe failed:", error);
        throw error;
    }
}

async function pressHome(udid, deviceType) {
    try {
        const response = await fetch('/api/press_home', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid, deviceType })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Home button press failed");
        return data;
    } catch (error) {
        console.error("Home button press failed:", error);
        throw error;
    }
}

async function pressBack(udid, deviceType) {
    try {
        const response = await fetch('/api/press_back', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid, deviceType })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Back button press failed");
        return data;
    } catch (error) {
        console.error("Back button press failed:", error);
        throw error;
    }
}

async function pressMenu(udid, deviceType) {
    try {
        const response = await fetch('/api/press_menu', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ udid, deviceType })
        });
        const data = await response.json();
        if (data.status !== "success") throw new Error(data.message || "Menu button press failed");
        return data;
    } catch (error) {
        console.error("Menu button press failed:", error);
        throw error;
    }
}
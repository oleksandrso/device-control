console.log("device-control.js loaded");

let deviceScreenSize = null;
let sessionActive = true;
let refreshInterval = null;
let activityTimeout = null;
let swipeStart = null;
let touchStartTime = null;

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

async function initializeSession(udid, deviceType) {
    console.log("Starting session for UDID:", udid, "Device Type:", deviceType);
    showLoading();
    try {
        await startSession(udid, deviceType);
        deviceScreenSize = await fetchScreenSize(udid);
        if (!deviceScreenSize) throw new Error("Failed to fetch screen size");
        console.log("Device screen size:", deviceScreenSize);
        sessionActive = true;

        // Immediately get the first screenshot
        refreshScreenshot(udid);

        // Then set up the normal refresh interval
        setRefreshInterval(10000);
    } catch (error) {
        sessionActive = false;
        console.error("Failed to initialize session:", error);
        document.body.innerHTML = '<h1>Error</h1><p>Failed to initialize session. <a href="/">Return to Home</a></p>';
    } finally {
        hideLoading();
    }
}

function setRefreshInterval(intervalMs) {
    // Clear any existing interval
    if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
    }

    console.log(`Setting refresh interval to ${intervalMs}ms`);

    // Set up a new interval
    refreshInterval = setInterval(() => {
        if (sessionActive) {
            refreshScreenshot(getUdid());
        } else {
            clearInterval(refreshInterval);
            refreshInterval = null;
            console.log("Session inactive, stopping screenshot refresh");
        }
    }, intervalMs);
}

function startActiveRefresh() {
    // Clear any existing interval and timeout
    if (refreshInterval) {
        clearInterval(refreshInterval);
        refreshInterval = null;
    }
    if (activityTimeout) {
        clearTimeout(activityTimeout);
        activityTimeout = null;
    }

    // Get the device UDID
    const udid = getUdid();

    // Immediately refresh the screenshot
    refreshScreenshot(udid);

    // Set up a fast refresh interval (1 second)
    console.log("Starting active refresh (1s)");
    let refreshCount = 0;
    refreshInterval = setInterval(() => {
        if (sessionActive) {
            refreshScreenshot(udid);
            refreshCount++;

            // After 3 refreshes (3 seconds), switch to idle mode
            if (refreshCount >= 3) {
                clearInterval(refreshInterval);
                refreshInterval = null;
                setRefreshInterval(10000);
                console.log("Switching to idle refresh (10s)");
            }
        } else {
            clearInterval(refreshInterval);
            refreshInterval = null;
            console.log("Session inactive, stopping screenshot refresh");
        }
    }, 1000);
}

async function tapOnImage(event) {
    event.preventDefault();
    console.log("Tap event triggered");
    if (!deviceScreenSize) {
        console.error("Device screen size not available");
        return;
    }
    const udid = getUdid();
    const deviceType = new URLSearchParams(window.location.search).get('type');
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
        await tap(udid, deviceX, deviceY, deviceType);
        startActiveRefresh();
    } catch (error) {
        sessionActive = false;
        console.error("Tap error:", error);
    }
}

function startSwipe(event) {
    event.preventDefault();
    if (!deviceScreenSize) {
        console.error("Device screen size not available");
        return;
    }
    console.log("Swipe start triggered");
    touchStartTime = Date.now();
    const img = event.target;
    const rect = img.getBoundingClientRect();
    swipeStart = {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
    };
    console.log(`Swipe started at image (${swipeStart.x}, ${swipeStart.y})`);
}

function updateSwipe(event) {
    // Swipe visualization, if needed
}

async function endSwipe(event) {
    event.preventDefault();
    console.log("Swipe end triggered");
    if (!swipeStart || !deviceScreenSize) {
        console.error("Swipe not started or device screen size not available");
        swipeStart = null;
        return;
    }
    const touchDuration = Date.now() - touchStartTime;
    const udid = getUdid();
    const deviceType = new URLSearchParams(window.location.search).get('type');
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

    // Игнорируем свайп, если длительность касания меньше 200 мс
    if (touchDuration < 200) {
        console.log("Swipe ignored: touch too short");
        swipeStart = null;
        return;
    }

    console.log(`Swipe from (${swipeStart.x}, ${swipeStart.y}) to (${endX}, ${endY}) -> Device (${deviceStartX}, ${deviceStartY}) to (${deviceEndX}, ${deviceEndY})`);

    try {
        await swipe(udid, deviceStartX, deviceStartY, deviceEndX, deviceEndY, deviceType);
        startActiveRefresh();
    } catch (error) {
        sessionActive = false;
        console.error("Swipe error:", error);
    } finally {
        swipeStart = null;
        touchStartTime = null;
    }
}

async function onHomePress(event) {
    console.log("home-btn clicked");
    const udid = getUdid();
    const deviceType = new URLSearchParams(window.location.search).get('type');
    console.log(`Pressing Home button for UDID: ${udid}, Device Type: ${deviceType}`);
    try {
        await pressHome(udid, deviceType);
        startActiveRefresh();
    } catch (error) {
        sessionActive = false;
        console.error("Home button error:", error);
    }
}

async function onBackPress() {
    console.log("Back button clicked");
    const udid = getUdid();
    const deviceType = new URLSearchParams(window.location.search).get('type');
    console.log("Pressing Back button for UDID:", udid);
    try {
        await pressBack(udid, deviceType);
        startActiveRefresh();
    } catch (error) {
        sessionActive = false;
        console.error("Back button error:", error);
    }
}

async function onMenuPress() {
    console.log("Menu button clicked");
    const udid = getUdid();
    const deviceType = new URLSearchParams(window.location.search).get('type');
    console.log("Pressing Menu button for UDID:", udid);
    try {
        await pressMenu(udid, deviceType);
        startActiveRefresh();
    } catch (error) {
        sessionActive = false;
        console.error("Menu button error:", error);
    }
}

function refreshScreenshot(udid) {
    const screen = document.getElementById('screen');
    const url = `/api/screenshot/${udid}?t=${new Date().getTime()}`;
    console.log("Requesting screenshot:", url);

    // Create a new Image object to load the screenshot in the background
    const img = new Image();
    img.onload = function() {
        // Only update the screen src if the session is still active
        if (sessionActive) {
            screen.src = url;
        }
    };
    img.onerror = async () => {
        console.error("Screenshot load failed, checking session...");
        sessionActive = await checkSessionStatus(udid);
    };
    img.src = url;
}

document.addEventListener('DOMContentLoaded', () => {
    console.log("DOMContentLoaded fired");
    const udid = getUdid();
    const params = new URLSearchParams(window.location.search);
    const deviceType = params.get('type');
    if (udid && deviceType) {
        initializeSession(udid, deviceType);
        const homeButton = document.getElementById('home-btn');
        if (homeButton) {
            console.log("Home button (#home-btn) found");
            homeButton.addEventListener('click', onHomePress);
        } else {
            console.error("Home button (#home-btn) not found in DOM");
        }
        const backButton = document.getElementById('back-device-btn');
        if (backButton) {
            console.log("Back button found");
            backButton.addEventListener('click', onBackPress);
        }
        const menuButton = document.getElementById('menu-btn');
        if (menuButton) {
            console.log("Menu button found");
            menuButton.addEventListener('click', onMenuPress);
        }
    } else {
        console.error("UDID or device type missing");
        document.body.innerHTML = '<h1>Error</h1><p>UDID or device type not specified. <a href="/">Return to Home</a></p>';
    }
});

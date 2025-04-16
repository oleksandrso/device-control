function selectDeviceFromUtils(deviceType, udid) {
    window.location.href = `/device-control.html?udid=${udid}&type=${deviceType}`;
}

function getUdid() {
    const params = new URLSearchParams(window.location.search);
    return params.get('udid');
}

function goBack() {
    try {
        window.location.replace('/');
        console.log("Navigating back to home page");
    } catch (error) {
        console.error("Error navigating back:", error);
    }
}
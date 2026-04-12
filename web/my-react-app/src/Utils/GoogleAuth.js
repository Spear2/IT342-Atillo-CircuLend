export function loadGoogleScript() {
  return new Promise((resolve, reject) => {
    if (window.google?.accounts?.id) return resolve();
    const script = document.createElement("script");
    script.src = "https://accounts.google.com/gsi/client";
    script.async = true;
    script.defer = true;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error("Failed to load Google SDK"));
    document.head.appendChild(script);
  });
}

export function requestGoogleIdToken(clientId) {
  return new Promise((resolve, reject) => {
    if (!window.google?.accounts?.id) return reject(new Error("Google SDK not initialized"));

    window.google.accounts.id.initialize({
      client_id: clientId,
      callback: (response) => {
        if (response?.credential) resolve(response.credential);
        else reject(new Error("No Google credential returned"));
      },
    });

    // Popup One Tap prompt
    window.google.accounts.id.prompt((notification) => {
      // Optional handling; callback above resolves when credential returns
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // fallback could be custom button flow if needed
      }
    });
  });
}
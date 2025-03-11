function authenticatedFetch(url, options = {}) {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...options.headers
    };

    return fetch(url, {
        ...options,
        headers
    }).then(response => {
        if (response.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login.html';
            return;
        }
        return response;
    });
}
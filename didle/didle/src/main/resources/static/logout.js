function logout() {
    fetch('/api/users/logout', {
        method: 'POST',
        credentials: 'include' // 쿠키를 포함하여 요청을 보냄
    })
        .then(response => response.json())
        .then(data => {
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
            window.location.href = '/login';
        })
        .catch(error => console.error('Error:', error));
}

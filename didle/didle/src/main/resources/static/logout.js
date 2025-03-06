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

function switchToUserMode() {
    // 사용자 모드로 전환하는 기능 구현
    window.location.href = '/'; // 사용자 모드로 전환하는 URL로 변경
}

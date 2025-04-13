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

document.addEventListener('DOMContentLoaded', function () {
    const currentPath = window.location.pathname; // 현재 URL 경로 가져오기
    const navLinks = document.querySelectorAll('.nav-link'); // 모든 nav-link 요소 선택

    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active'); // 현재 페이지 링크에 active 클래스 추가
        } else {
            link.classList.remove('active'); // 다른 링크에서는 active 클래스 제거
        }
    });
});


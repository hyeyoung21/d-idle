function switchToUserMode() {
    // 사용자 모드로 전환하는 기능 구현
    window.location.href = 'index.html'; // 사용자 모드로 전환하는 URL로 변경
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

async function updateAuthState() {
    const loggedInUserDiv = document.getElementById('logged-in-user');
    const guestUserDiv = document.getElementById('guest-user');
    const userGreetingSpan = document.getElementById('user-greeting');
    const adminLinkContainer = document.getElementById('admin-link-container');

    try {
        // 수정된 엔드포인트 호출
        const response = await fetch('/api/users/me');

        if (response.ok) {
            const user = await response.json();
            // 로그인 상태 UI 업데이트
            userGreetingSpan.textContent = `${user.username}님 환영합니다!`;
            // 역할 비교: 백엔드에서 보낸 값 ('CUSTOMER', 'ADMIN')과 비교
            if (user.role === 'ADMIN') { // 대소문자 주의! 백엔드 응답과 일치
                adminLinkContainer.style.display = 'block';
            } else {
                adminLinkContainer.style.display = 'none';
            }
            loggedInUserDiv.style.display = 'flex';
            guestUserDiv.style.display = 'none';
        } else {
            // 비로그인 상태 UI 업데이트 (401 등)
            loggedInUserDiv.style.display = 'none';
            guestUserDiv.style.display = 'block';
        }
    } catch (error) {
        console.error('Error fetching user status:', error);
        // 네트워크 오류 등 발생 시 비로그인 상태로 처리
        loggedInUserDiv.style.display = 'none';
        guestUserDiv.style.display = 'block';
    }
}

// logout 함수는 이전과 동일하게 사용 가능
async function logout() {
    try {
        const response = await fetch('/api/users/logout', { method: 'POST' });

        if (response.ok) { // 204 No Content도 response.ok 는 true
            updateAuthState(); // 상태 다시 확인하여 UI 업데이트
            // 또는 window.location.reload(); // 페이지 새로고침
        } else {
            console.error('Logout failed:', response.status);
            alert('로그아웃에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error during logout:', error);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
}



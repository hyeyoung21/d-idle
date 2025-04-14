// --- 비즈니스 인증 상태 업데이트 로직 ---
async function updateBusinessAuthState() {
    const loggedInBusinessDiv = document.getElementById('logged-in-business');
    const businessGreetingSpan = document.getElementById('business-greeting');
    // const guestBusinessDiv = document.getElementById('guest-business'); // 비로그인 영역은 보통 사용 안 함

    try {
        // 수정된 엔드포인트 호출
        const response = await fetch('/api/business/me');

        if (response.ok) {
            const business = await response.json();
            // 로그인 상태 UI 업데이트
            businessGreetingSpan.textContent = `${business.businessName}님 환영합니다!`;
            loggedInBusinessDiv.style.display = 'flex';
            // if (guestBusinessDiv) guestBusinessDiv.style.display = 'none';
        } else {
            // 비로그인 상태 처리 -> 비즈니스 페이지는 보통 로그인 필수이므로 리다이렉션
            console.warn('Business not authenticated, redirecting to login.');
            window.location.href = 'login.html'; // 비즈니스 로그인 페이지 경로
        }
    } catch (error) {
        console.error('Error fetching business status:', error);
        // 오류 발생 시 로그인 페이지로 리다이렉션
        window.location.href = 'login.html'; // 비즈니스 로그인 페이지 경로
    }
}

// --- 비즈니스 로그아웃 로직 ---
async function logoutBusiness() { // 함수 이름 변경
    try {
        const response = await fetch('/api/business/logout', { method: 'POST' }); // 비즈니스 로그아웃 엔드포인트

        if (response.ok || response.status === 204) { // 204 No Content도 성공 처리
            // 로그아웃 성공 시 로그인 페이지로 리다이렉션
            alert('로그아웃되었습니다.');
            window.location.href = 'login.html'; // 비즈니스 로그인 페이지 경로
        } else {
            console.error('Business logout failed:', response.status);
            alert('로그아웃에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error during business logout:', error);
        alert('로그아웃 중 오류가 발생했습니다.');
    }
}

// --- 네비게이션 활성화 로직 (사용자 main.js와 동일하게 적용 가능) ---
document.addEventListener('DOMContentLoaded', function () {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link'); // 헤더 내부 링크만 선택

    navLinks.forEach(link => {
        // href 속성값과 현재 경로 비교 (상대 경로/절대 경로 고려)
        const linkPath = link.getAttribute('href');
        // 루트 경로 특별 처리 및 정확한 경로 비교
        if (linkPath === currentPath || (linkPath === '/business' && currentPath.startsWith('/business'))) {
            // '/business' 링크는 하위 경로에서도 활성화될 수 있도록 startsWith 사용 (선택 사항)
            if(currentPath === '/business' || !link.getAttribute('href').includes('/')){ // 정확히 /business 이거나 하위 메뉴가 아닐 때만 active
                link.classList.add('active');
            } else if (linkPath !== '/business' && currentPath.startsWith(linkPath)) { // 다른 하위 메뉴
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        } else {
            link.classList.remove('active');
        }
    });

    // 페이지 로드 시 비즈니스 인증 상태 확인
    updateBusinessAuthState();
});
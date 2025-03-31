document.addEventListener('DOMContentLoaded', function() {
    const chatbotContainer = document.querySelector('.chatbot-container');
    const toggleBtn = document.querySelector('.chatbot-toggle-btn');
    const chatbotWindow = document.querySelector('.chatbot-window');
    const closeBtn = document.querySelector('.chatbot-close-btn');

    // 챗봇 열기/닫기 토글
    function openChatbot() {
        chatbotWindow.classList.add('active'); // 챗봇 창 활성화
        toggleBtn.style.display = 'none'; // 챗봇 열기 버튼 숨김
    }

    function closeChatbot() {
        chatbotWindow.classList.remove('active'); // 챗봇 창 비활성화
        toggleBtn.style.display = 'block'; // 챗봇 열기 버튼 표시
    }

    toggleBtn.addEventListener('click', openChatbot); // 챗봇 열기 버튼 클릭 시 열림
    closeBtn.addEventListener('click', closeChatbot); // X 버튼 클릭 시 닫힘

    // WebChat 렌더링
    const styleOptions = {
        backgroundColor: '#ffffff',
        bubbleBackground: '#f0f4ff',
        bubbleBorderRadius: 12,
        bubbleFromUserBackground: '#e0f7fa',
        bubbleMaxWidth: 280,
        botAvatarInitials: 'B',
        userAvatarInitials: '나'
    };

    window.WebChat.renderWebChat({
        directLine: window.WebChat.createDirectLine({
            token: 'D1tWEYY0HJWELRWv65PGQmWdqGJgDempg5TlywpFwGVgrmCVCvolJQQJ99BCACHYHv6AArohAAABAZBS23iz.F2nxfjHkwlQFdDiXs0qd5LWJlRiMadVhtMUJrhoUCfwGTjN4vkSYJQQJ99BCAC3pKaRAArohAAABAZBSDgin'
        }),
        styleOptions,
        locale: 'ko-KR',
        userID: Date.now().toString(),
        username: '사용자',
        botAvatarInitials: 'B',
        userAvatarInitials: '나'
    }, document.getElementById('webchat'));
});

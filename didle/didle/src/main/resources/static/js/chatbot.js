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

    // WebChat 스타일 옵션
    const styleOptions = {
        backgroundColor: '#ffffff',
        bubbleBackground: '#f0f4ff',
        bubbleBorderRadius: 12,
        bubbleFromUserBackground: '#e0f7fa',
        bubbleMaxWidth: 280,
        botAvatarInitials: 'B',
        userAvatarInitials: '나'
    };

    // 사용자 정보 가져오기
    fetch('/api/users/current')
        .then(response => {
            if (!response.ok) {
                throw new Error('사용자 정보를 가져오는데 실패했습니다');
            }
            return response.json();
        })
        .then(userData => {
            console.log('사용자 정보 로드 성공:', userData);

            // WebChat 렌더링 (사용자 정보 포함)
            window.WebChat.renderWebChat({
                directLine: window.WebChat.createDirectLine({
                    token: 'D1tWEYY0HJWELRWv65PGQmWdqGJgDempg5TlywpFwGVgrmCVCvolJQQJ99BCACHYHv6AArohAAABAZBS23iz.F2nxfjHkwlQFdDiXs0qd5LWJlRiMadVhtMUJrhoUCfwGTjN4vkSYJQQJ99BCAC3pKaRAArohAAABAZBSDgin'
                }),
                styleOptions,
                locale: 'ko-KR',
                userID: userData.id || Date.now().toString(),
                username: userData.username || '사용자',
                botAvatarInitials: 'B',
                userAvatarInitials: userData.username ? userData.username.charAt(0) : '나',

                // 사용자 정보를 봇에게 전달하는 store 설정
                store: window.WebChat.createStore({}, ({ dispatch }) => next => action => {
                    if (action.type === 'DIRECT_LINE/CONNECT_FULFILLED') {
                        // 봇에게 사용자 정보 전달
                        dispatch({
                            type: 'WEB_CHAT/SEND_EVENT',
                            payload: {
                                name: 'userInfo',
                                value: {
                                    id: userData.id,
                                    username: userData.username,
                                    email: userData.email,
                                    fullName: userData.fullName,
                                    userType: userData.userType
                                    // 필요한 다른 정보들 추가 가능
                                }
                            }
                        });
                    }
                    return next(action);
                })
            }, document.getElementById('webchat'));
        })
        .catch(error => {
            console.error('사용자 정보 로드 실패:', error);

            // 오류 시 기본 정보로 WebChat 초기화
            window.WebChat.renderWebChat({
                directLine: window.WebChat.createDirectLine({
                    token: 'D1tWEYY0HJWELRWv65PGQmWdqGJgDempg5TlywpFwGVgrmCVCvolJQQJ99BCACHYHv6AArohAAABAZBS23iz.F2nxfjHkwlQFdDiXs0qd5LWJlRiMadVhtMUJrhoUCfwGTjN4vkSYJQQJ99BCAC3pKaRAArohAAABAZBSDgin'
                }),
                styleOptions,
                locale: 'ko-KR',
                userID: Date.now().toString(),
                username: '손님',
                botAvatarInitials: 'B',
                userAvatarInitials: '?'
            }, document.getElementById('webchat'));
        });
});

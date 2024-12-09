document.addEventListener('DOMContentLoaded', () => {
    const usernamePage = document.getElementById('username-page');
    const chatPage = document.getElementById('chat-page');
    const usernameForm = document.getElementById('usernameForm');
    const messageForm = document.getElementById('messageForm');
    const messageArea = document.getElementById('messageArea');
    const connectingElement = document.querySelector('.connecting');

    let username = null;
    let stompClient = null;

    // Kết nối tới WebSocket
    const connect = () => {
        const socket = new SockJS('http://localhost:8080/ws'); // Cập nhật URL theo server của bạn
        stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            connectingElement.classList.add('hidden');
            stompClient.subscribe('/topic/public', onMessageReceived); // đăng ký topic
            stompClient.send('/app/addUser', {}, JSON.stringify({sender: username, type: 'JOIN'})); // // Phải Stringify bởi STOMP chỉ hỗ trợ text
        }, onError);
    };

    // Xử lý error khi không thể kết nối tới WebSocker server
    const onError = (error) => {
        connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh and try again.';
        connectingElement.style.color = 'red';
        console.error('WebSocket Error:', error);
    };

    // Gửi message tới Message Broker (ở websocket server)
    const sendMessage = (event) => {
        event.preventDefault();

        // Lấy nội dung (input) message sẽ gửi
        const messageInput = document.getElementById('message');
        const messageContent = messageInput.value.trim();

        if (messageContent && stompClient) {
            const chatMessage = {
                sender: username,
                content: messageContent,
                type: 'CHAT'
            };
            stompClient.send('/app/sendMessage', {}, JSON.stringify(chatMessage)); // Phải Stringify bởi STOMP chỉ hỗ trợ text
            messageInput.value = ''
        }
    };

    // Hàm tạo phần tử tin nhắn
function createMessage(sender, message) {
    // Tạo một phần tử <li> cho tin nhắn
    const messageItem = document.createElement('li');
    messageItem.classList.add('message-item');
  
    // Tạo phần tử avatar chứa chữ cái đầu tiên của người gửi
    const avatar = document.createElement('div');
    avatar.classList.add('avatar');
    avatar.textContent = sender.charAt(0).toUpperCase(); // Lấy chữ cái đầu tiên của tên sender và viết hoa
  
    // Tạo phần tử message để chứa tên người gửi và tin nhắn
    const messageContent = document.createElement('div');
    messageContent.classList.add('message');
  
    const name = document.createElement('span');
    name.classList.add('name');
    name.textContent = sender; // Gán tên người gửi
  
    const text = document.createElement('span');
    text.classList.add('text');
    text.textContent = message; // Gán tin nhắn
  
    // Thêm name và text vào messageContent
    messageContent.appendChild(name);
    messageContent.appendChild(text);
  
    // Thêm avatar và messageContent vào messageItem
    messageItem.appendChild(avatar);
    messageItem.appendChild(messageContent);
  
    // Thêm messageItem vào messageArea
    messageArea.appendChild(messageItem);
  }
  


    // Hàm Xử lý sự kiện các message truyền đến từ server đến nó
    const onMessageReceived = (payload) => {
        const message = JSON.parse(payload.body);
        const messageElement = document.createElement('li');

        if (message.type === 'JOIN') {
            messageElement.classList.add('event-message');
            message.content = `${message.sender} joined the chat`;

            const textElement = document.createElement('span');
            textElement.textContent = message.content;
            messageElement.appendChild(textElement);
            messageArea.appendChild(messageElement);
        } else if (message.type === 'CHAT') { 
            createMessage(message.sender, message.content)
        } else if (message.type === 'LEAVE') {
            messageElement.classList.add('event-message');
            message.content = `${message.sender} left the chat`;

            const textElement = document.createElement('span');
            textElement.textContent = message.content;
            messageElement.appendChild(textElement);
            messageArea.appendChild(messageElement);
        }
        messageArea.scrollTop = messageArea.scrollHeight;
    };


    // Xử lý sự kiện tham gia vào Chat ( /app/addUser ý)
    usernameForm.addEventListener('submit', (event) => {
        event.preventDefault();
        const usernameInput = document.getElementById('name');
        username = usernameInput.value.trim();

        if (username) {
            usernamePage.classList.add('hidden');
            chatPage.classList.remove('hidden');
            connect();
        }
    });

    // Xử lý sự kiện gửi tin nhắn
    messageForm.addEventListener('submit', sendMessage);
});

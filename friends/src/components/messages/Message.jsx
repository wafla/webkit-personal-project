import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { format } from 'date-fns';
import axios from 'axios';
import { AuthContext } from '../member/AuthContext';

const Message = () => {
    const { name } = useParams();
    const navigate = useNavigate();
    const [messages, setMessages] = useState([]);
    const { user } = useContext(AuthContext);
    const [newMessage, setNewMessage] = useState('');
    const [profile, setProfile] = useState('');
    const [receiverId, setReceiverId] = useState();
    const [chatUsers, setChatUsers] = useState([]);

    useEffect(() => {
        getUser();
        if (receiverId) {
            fetchMessages();
        }
        if (user?.id === receiverId) {
            fetchChatUsers();
        }
    }, [receiverId, name]);

    const formatDate = (dateString) => {
        return format(new Date(dateString), 'yyyy-MM-dd HH:mm:ss');
    };

    const getUser = async () => {
        try {
            const response = await axios.get(`http://localhost:7777/api/users/${name}`);
            setProfile(response.data.data.fileName);
            setReceiverId(response.data.data.id);
        } catch (error) {
            console.error('profile 정보를 불러오지 못했습니다.: ', error);
        }
    };

    const fetchMessages = async () => {
        try {
            const response = await axios.get(`http://localhost:7777/api/messages`, {
                params: { userId: user.id, otherUserId: receiverId },
            });
            console.log(response);

            setMessages(response.data.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp)));
        } catch (error) {
            console.error('메시지를 불러오지 못했습니다.: ', error);
        }
    };

    const fetchChatUsers = async () => {
        try {
            const response = await axios.get(`http://localhost:7777/api/messages/chat-users`, {
                params: { userId: user.id },
            });
            setChatUsers(response.data);
        } catch (error) {
            console.error('대화한 사용자 목록을 불러오지 못했습니다.: ', error);
        }
    };

    const handleMessageSubmit = async () => {
        if (!user) {
            alert('로그인하세요');
            return;
        }
        if (!newMessage.trim()) {
            alert('메시지를 입력하세요.');
            return;
        }
        try {
            if (!receiverId) {
                alert('존재하지 않는 유저입니다.');
                return;
            }
            await axios.post(`http://localhost:7777/api/messages/send`, {
                content: newMessage,
                senderId: user.id,
                receiverId: receiverId,
            });
            setNewMessage('');
            fetchMessages();
        } catch (error) {
            alert('메시지 전송 실패: ' + error.message);
        }
    };
    if (user == null)
        return (
            <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                <p>로그인하세요</p>
            </div>
        );
    return (
        <div className="container">
            <div
                className="d-flex justify-content-center"
                style={{ maxWidth: '600px', margin: '0 auto', padding: '10px' }}
            >
                <Link
                    to={`/profile/${name}`}
                    className="d-flex align-items-center"
                    style={{ textDecoration: 'none', color: 'inherit' }}
                >
                    <img
                        src={`http://localhost:7777/uploads/${profile || 'noimage.png'}`}
                        style={{ width: '50px', height: '50px', borderRadius: '50%' }}
                    />
                    <h5 className="mb-0 ms-3">{name}</h5>
                </Link>
            </div>
            <hr style={{ width: '600px', margin: '0 auto' }} />
            {user.id != receiverId && (
                <div>
                    {messages.length > 0 && (
                        <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                            {messages.map((msg) => (
                                <div
                                    key={msg.id}
                                    style={{
                                        padding: '10px',
                                        background: msg.senderId === user.id ? '#e3f2fd' : '#f1f1f1',
                                        borderRadius: '10px',
                                        marginBottom: '10px',
                                    }}
                                >
                                    <Link
                                        to={`/profile/${msg.sender.name}`}
                                        className="d-flex align-items-center"
                                        style={{ textDecoration: 'none', color: 'inherit', width: 'fit-content' }}
                                    >
                                        <img
                                            src={`http://localhost:7777/uploads/${
                                                msg.sender.fileName || 'noimage.png'
                                            }`}
                                            style={{ width: '30px', height: '30px', borderRadius: '50%' }}
                                        />
                                        <h5 className="mb-0 ms-3">{msg.sender.name}</h5>
                                    </Link>
                                    <p>
                                        <strong>{msg.senderName}</strong>
                                    </p>
                                    <div className="d-flex justify-content-between">
                                        <p>{msg.content}</p>
                                        <small className="text-muted">{formatDate(msg.wdate)}</small>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                    {!messages.length && (
                        <p
                            className="mt-3 mb-3"
                            style={{ width: '600px', margin: '0 auto' }}
                        >
                            메시지가 없습니다.
                        </p>
                    )}
                    <hr style={{ width: '600px', margin: '0 auto' }} />
                    <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                        <h5>메시지 작성</h5>
                        <textarea
                            className="form-control my-2"
                            rows="3"
                            placeholder="메시지를 입력하세요..."
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                        />
                        <div className="d-flex">
                            <button
                                className="btn btn-primary ms-auto"
                                onClick={handleMessageSubmit}
                            >
                                메시지 전송
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {user.id == receiverId && (
                <div
                    style={{
                        maxWidth: '600px',
                        margin: '0 auto',
                        backgroundColor: '#e3f2fd',
                        borderRadius: '3%',
                    }}
                >
                    {chatUsers.length > 0 ? (
                        chatUsers.map((chatUser) => (
                            <Link
                                key={chatUser.id}
                                to={`/message/${chatUser.name}`}
                                className="d-flex align-items-center my-2"
                                style={{ textDecoration: 'none', color: 'inherit' }}
                            >
                                <div
                                    className="d-flex align-items-center justify-content-between"
                                    style={{ width: '100%', padding: '10px', borderBottom: '1px solid #ddd' }}
                                >
                                    <div
                                        className="d-flex align-items-center"
                                        style={{ flex: 1 }}
                                    >
                                        <img
                                            src={`http://localhost:7777/uploads/${chatUser.fileName || 'noimage.png'}`}
                                            style={{
                                                width: '40px',
                                                height: '40px',
                                                borderRadius: '50%',
                                                marginRight: '10px',
                                            }}
                                        />
                                        <h6 className="mb-0">{chatUser.name}</h6>
                                    </div>
                                    <div
                                        className="text-truncate"
                                        style={{ flex: 2, textAlign: 'center' }}
                                    >
                                        <h6
                                            className="mb-0"
                                            style={{
                                                whiteSpace: 'nowrap',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                            }}
                                        >
                                            {chatUser.lastMessage}
                                        </h6>
                                    </div>
                                    <div style={{ flex: 1, textAlign: 'right' }}>
                                        <h6 className="mb-0">{formatDate(chatUser.lastMessageTime)}</h6>
                                    </div>
                                </div>
                            </Link>
                        ))
                    ) : (
                        <p>대화한 사용자가 없습니다.</p>
                    )}
                </div>
            )}
        </div>
    );
};

export default Message;

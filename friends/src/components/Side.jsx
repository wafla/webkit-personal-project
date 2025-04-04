import React, { useContext } from 'react';
import { Stack, Button, ListGroup } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from './member/AuthContext';
import axiosInstance from './member/axiosInstance';
export default function Side({ onShowLogin }) {
    const { user, logoutUser } = useContext(AuthContext);
    console.log(`Side컴포넌트에서 user: ${user}`);

    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            const url = `http://localhost:7777/api/auth/logout`;
            await axiosInstance.post(url, { email: user.email });
        } catch (error) {
            alert('로그아웃 처리 중 에러: ' + error);
        }

        logoutUser();
        sessionStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        navigate('/');
    };

    return (
        <Stack
            gap={2}
            className="col-md-10 mx-auto"
        >
            <Button
                variant="primary"
                as={Link}
                to="/"
            >
                Home
            </Button>
            {!user && (
                <Button
                    variant="outline-success"
                    as={Link}
                    to="/signup"
                >
                    SignUp
                </Button>
            )}
            {user && <div className="alert alert-danger">{user.name}님 로그인 중...</div>}
            {user ? (
                <Button
                    variant="outline-success"
                    onClick={handleLogout}
                >
                    Logout
                </Button>
            ) : (
                <Button
                    variant="outline-success"
                    onClick={() => onShowLogin(true)}
                    as={Link}
                >
                    SignIn
                </Button>
            )}
        </Stack>
    );
}

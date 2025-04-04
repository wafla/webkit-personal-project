import { useState, useContext } from 'react';
import { Container, Nav, Navbar } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import LoginModal from './member/LoginModal';
import { AuthContext } from './member/AuthContext';
import axiosInstance from './member/axiosInstance';
export default function Header() {
    const [showModal, setShowModal] = useState(false);
    const { user, logoutUser } = useContext(AuthContext);
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
        localStorage.removeItem('user');
        alert('로그아웃 되었습니다.');
    };

    return (
        <>
            <Navbar
                collapseOnSelect
                expand="lg"
                className="bg-body-tertiary"
                fixed="top"
                bg="dark"
                data-bs-theme="dark"
            >
                <Container>
                    <Navbar.Brand
                        as={Link}
                        to="/"
                    >
                        Every Friends
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                    <Navbar.Collapse id="responsive-navbar-nav">
                        <Nav className="ms-auto">
                            {user ? (
                                <>
                                    <Nav.Link onClick={handleLogout}>Logout</Nav.Link>
                                    <Nav.Link
                                        as={Link}
                                        to={`/postForm`}
                                    >
                                        Post
                                    </Nav.Link>
                                    <Nav.Link
                                        as={Link}
                                        to={`/message/${user.name}`}
                                    >
                                        Message
                                    </Nav.Link>
                                    <Nav.Link
                                        as={Link}
                                        to={`/profile/${user.name}`}
                                    >
                                        MyPage
                                    </Nav.Link>
                                </>
                            ) : (
                                <>
                                    <Nav.Link onClick={() => setShowModal(true)}>Login</Nav.Link>
                                    <Nav.Link
                                        as={Link}
                                        to={`/signup`}
                                    >
                                        SignUp
                                    </Nav.Link>
                                </>
                            )}
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
            <LoginModal
                show={showModal}
                setShow={setShowModal}
            />
        </>
    );
}

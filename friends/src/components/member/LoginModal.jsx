import React, { useState, useRef, useContext } from 'react';
import { Modal, Button, Row, Col, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

import { AuthContext } from './AuthContext';

export default function LoginModal({ show, setShow }) {
    const [loginUser, setLoginUser] = useState({ email: '', passwd: '' });

    const navigate = useNavigate();

    const { loginAuthUser } = useContext(AuthContext);

    const idRef = useRef(null);
    const pwRef = useRef(null);

    const { email, passwd } = loginUser;

    console.log(loginUser.email, loginUser['email']);

    const onChangeHandler = (e) => {
        setLoginUser({ ...loginUser, [e.target.name]: e.target.value });
    };

    const onSubmitHandler = (e) => {
        e.preventDefault();
        if (!email) {
            alert('아이디를 입력하세요');
            idRef.current.focus();
            return;
        }
        if (!passwd) {
            alert('비밀번호를 입력하세요');
            pwRef.current.focus();
            return;
        }
        requestLogin();
    };

    const requestLogin = async () => {
        let url = `http://localhost:7777/api/auth/login`;
        try {
            const response = await axios.post(url, loginUser);
            const { result } = response.data;

            if (result === 'success') {
                const authUser = response.data.data;
                alert(response.data.message + ` ${authUser.name}님 환영합니다`);
                loginAuthUser(authUser);
                const { accessToken, refreshToken } = response.data;
                sessionStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
                localStorage.setItem('user', JSON.stringify(authUser));
                inputClear();
                setShow(false);
            } else {
                const { message } = response.data;
                alert(message);
                inputClear();
                idRef.current.focus();
            }
        } catch (error) {
            alert('Error: ' + error);
            inputClear();
            setShow(false);
        }
    };

    const inputClear = () => {
        setLoginUser({ ...loginUser, email: '', passwd: '' });
    };

    return (
        <div>
            <Modal
                show={show}
                onHide={() => setShow(false)}
                centered
            >
                <Modal.Header closeButton>
                    <Modal.Title>Login</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Row className="LoginForm">
                        <Col
                            className="p-4 mx-auto"
                            xs={10}
                            sm={10}
                            md={8}
                        >
                            <Form onSubmit={onSubmitHandler}>
                                <Form.Group className="mb-3">
                                    <Form.Label>ID</Form.Label>
                                    <Form.Control
                                        type="email"
                                        id="email"
                                        ref={idRef}
                                        name="email"
                                        onChange={onChangeHandler}
                                        value={loginUser.email}
                                        placeholder="ID (email)"
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Password</Form.Label>
                                    <Form.Control
                                        type="password"
                                        id="passwd"
                                        ref={pwRef}
                                        name="passwd"
                                        onChange={onChangeHandler}
                                        value={loginUser.passwd}
                                        placeholder="Password"
                                    />
                                </Form.Group>
                                <div className="d-grid gap-2">
                                    <Button
                                        type="submit"
                                        variant="outline-success"
                                    >
                                        Login
                                    </Button>
                                </div>
                            </Form>
                        </Col>
                    </Row>
                </Modal.Body>
            </Modal>
        </div>
    );
}

import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Container, Row, Col } from 'react-bootstrap';
import { useState, useEffect, useContext } from 'react';
import Header from './components/Header';
import Side from './components/Side';
// import Home from './components/Home';
// import PageNotFound from './components/PageNotFound';
// import SignUp from './components/member/SignUp';
// import MyPage from './components/member/MyPage';
import { AuthContext } from './components/member/AuthContext';
import axiosInstance from './components/member/axiosInstance';
import PostApp from './components/posts/PostApp';
import Post from './components/posts/Post';
import Profile from './components/member/Profile';
import SignUp from './components/member/SignUp';
import PostForm from './components/posts/PostForm';
import Message from './components/messages/Message';

function App() {
    const { loginAuthUser } = useContext(AuthContext);

    return (
        <div className="container py-5">
            <BrowserRouter>
                <Container>
                    <Row>
                        <Col className="mb-5">
                            <Header />
                        </Col>
                    </Row>
                    <Row>
                        {/* <Col
                            xs={12}
                            sm={9}
                            md={9}
                            lg={9}
                        > */}
                        <Routes>
                            <Route
                                path="/"
                                element={<PostApp />}
                            />
                            <Route
                                path="/post/:id"
                                element={<Post />}
                            />
                            <Route
                                path="/profile/:nickname"
                                element={<Profile />}
                            />
                            <Route
                                path="/signup"
                                element={<SignUp />}
                            />
                            <Route />
                            <Route
                                path="/postForm"
                                element={<PostForm />}
                            ></Route>
                            <Route
                                path="/message/:name"
                                element={<Message />}
                            ></Route>
                        </Routes>
                        {/* </Col> */}
                    </Row>
                </Container>
            </BrowserRouter>
        </div>
    );
}

export default App;

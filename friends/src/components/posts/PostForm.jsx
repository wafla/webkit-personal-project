import React, { useState, useContext } from 'react';
import { Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import { AuthContext } from '../member/AuthContext';

const PostForm = () => {
    const { user, logoutUser } = useContext(AuthContext);

    const [formData, setFormData] = useState({
        title: '',
        content: '',
        file: null,
    });

    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleFileChange = (e) => {
        if (e.target.files.length > 0) {
            setFormData({ ...formData, file: e.target.files[0] });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (user == null) {
            alert('로그인하세요');
            return;
        }
        try {
            const data = new FormData(); //파일 업로드 할 때는 FormData객체에 담아서 보낸다
            data.append('userId', user.id);
            data.append('name', user.name);
            data.append('title', formData.title);
            data.append('content', formData.content);
            if (formData.file) {
                data.append('file', formData.file);
            }

            const response = await axios.post('http://localhost:7777/api/posts', data, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            console.log(JSON.stringify(response));
            alert('글 작성 성공! 메인 화면으로 돌아갑니다.');
            navigate('/');
        } catch (error) {
            console.error('서버 요청 오류:', error);
            alert('서버 요청 중 오류가 발생했습니다.');
        }
    };

    return (
        <div
            className="post-form"
            style={{ maxWidth: '600px', margin: '0 auto' }}
        >
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="title">
                    <Form.Label>Title</Form.Label>
                    <Form.Control
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        required
                    />
                </Form.Group>
                <Form.Group controlId="content">
                    <Form.Label>Content</Form.Label>
                    <Form.Control
                        as="textarea"
                        name="content"
                        value={formData.content}
                        rows={8}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group controlId="file">
                    <Form.Label>File</Form.Label>
                    <Form.Control
                        type="file"
                        name="file"
                        accept="image/*"
                        onChange={handleFileChange}
                    />
                </Form.Group>
                <Button
                    variant="primary"
                    type="submit"
                    className="mt-3"
                >
                    Submit
                </Button>
            </Form>
        </div>
    );
};

export default PostForm;

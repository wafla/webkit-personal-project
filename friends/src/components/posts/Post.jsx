import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import postsData from './data';
import { format } from 'date-fns';
import axios from 'axios';
import { AuthContext } from '../member/AuthContext';

const Post = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [post, setPost] = useState();
    const { user, logoutUser } = useContext(AuthContext);

    const [isEditing, setIsEditing] = useState(false);
    const [editedTitle, setEditedTitle] = useState('');
    const [editedContent, setEditedContent] = useState('');
    const [editedFile, setEditedFile] = useState(null);

    const [newComment, setNewComment] = useState('');

    const formatDate = (dateString) => {
        return format(new Date(dateString), 'yyyy-MM-dd HH:mm:ss');
    };

    useEffect(() => {
        fetchPost();
    }, []);

    useEffect(() => {
        if (post) {
            setEditedTitle(post.title);
            setEditedContent(post.content);
        }
    }, [post]);

    const fetchUserProfile = async (name) => {
        try {
            const response = await axios.get(`http://localhost:7777/api/users/${name}`);
            // console.log(response.data.data.fileName);

            return response.data.data.fileName;
        } catch (error) {
            console.error('postList 유저 정보를 불러오지 못했습니다.: ', error);
            return null;
        }
    };

    const fetchPost = async () => {
        let url = `http://localhost:7777/api/posts/${id}`;
        try {
            const response = await axios.get(url);
            console.log(response);
            setPost(response.data);

            const profile = await fetchUserProfile(response.data.name);
            // console.log('fileName: ' + profile);
            setPost((prev) => ({ ...prev, profile }));
        } catch (error) {
            alert('error: ' + error.message);
        }
    };

    const deletePost = async () => {
        const confirmDelete = window.confirm('정말 삭제하시겠습니까?');
        if (!confirmDelete) return;

        let url = `http://localhost:7777/api/posts/${id}`;
        try {
            const response = await axios.delete(url);
            console.log(response);
            alert('삭제되었습니다.');
            navigate('/');
        } catch (error) {
            alert('error: ' + error.message);
        }
    };

    const handleSave = async () => {
        const formData = new FormData();
        const requestPayload = {
            title: editedTitle,
            content: editedContent,
        };

        formData.append('request', new Blob([JSON.stringify(requestPayload)], { type: 'application/json' }));
        if (editedFile) {
            formData.append('file', editedFile);
        }

        try {
            const response = await axios.put(`http://localhost:7777/api/posts/${id}`, formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            });
            console.log(JSON.stringify(response));
            alert('수정 완료!');
            setIsEditing(false);
            fetchPost();
        } catch (error) {
            alert('수정 실패: ' + error.message);
        }
    };

    const handleFileChange = (e) => {
        setEditedFile(e.target.files[0]);
    };

    const handleCommentSubmit = async () => {
        if (user == null) {
            alert('로그인하세요');
            return;
        }
        if (!newComment.trim()) {
            alert('댓글을 입력하세요.');
            return;
        }
        try {
            const response = await axios.post(`http://localhost:7777/api/comment`, {
                content: newComment,
                userId: user.id,
                postId: id,
            });

            console.log(response);
            alert('댓글이 추가되었습니다.');
            setNewComment('');
            fetchPost();
        } catch (error) {
            alert('댓글 작성 실패: ' + error.message);
        }
    };

    const deleteComment = async (id) => {
        const confirmDelete = window.confirm('정말 삭제하시겠습니까?');
        if (!confirmDelete) return;

        try {
            const response = await axios.delete(`http://localhost:7777/api/comment/${id}`);

            alert('댓글이 삭제되었습니다.');
            fetchPost();
        } catch (error) {
            alert('댓글 삭제 실패: ' + error.message);
        }
    };

    if (!post) return <p>포스트를 찾을 수 없습니다.</p>;

    return (
        <div class="container">
            <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                {isEditing ? (
                    <>
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <button
                                className="btn btn-outline-secondary"
                                onClick={() => navigate(-1)}
                            >
                                ← Back
                            </button>
                            <div>
                                {user && user.name == post.name && (
                                    <button
                                        className="btn btn-outline-primary mx-2"
                                        onClick={() => {
                                            handleSave();
                                        }}
                                    >
                                        Save
                                    </button>
                                )}
                                {user && user.name == post.name && (
                                    <button
                                        className="btn btn-outline-danger"
                                        onClick={() => setIsEditing(false)}
                                    >
                                        Cancle
                                    </button>
                                )}
                            </div>
                        </div>
                        <div>
                            <input
                                className="form-control my-2"
                                type="text"
                                value={editedTitle}
                                onChange={(e) => setEditedTitle(e.target.value)}
                            />
                            <textarea
                                className="form-control my-2"
                                value={editedContent}
                                rows={8}
                                onChange={(e) => setEditedContent(e.target.value)}
                            />
                            <input
                                className="form-control my-2"
                                type="file"
                                onChange={handleFileChange}
                                accept="image/*"
                            />
                        </div>
                    </>
                ) : (
                    <>
                        <div className="d-flex justify-content-between align-items-center mb-3">
                            <button
                                className="btn btn-outline-secondary"
                                onClick={() => navigate(-1)}
                            >
                                ← Back
                            </button>
                            <div>
                                {user && user.name == post.name && (
                                    <button
                                        className="btn btn-outline-primary mx-2"
                                        onClick={() => setIsEditing(true)}
                                    >
                                        Edit
                                    </button>
                                )}
                                {user && user.name == post.name && (
                                    <button
                                        className="btn btn-outline-danger"
                                        onClick={() => deletePost()}
                                    >
                                        Delete
                                    </button>
                                )}
                            </div>
                        </div>
                        <h1>{post.title}</h1>
                        <div className="d-flex align-items-center gap-2">
                            <Link
                                key={post.id}
                                className="d-flex align-items-center"
                                to={`/profile/${post.name}`}
                                style={{
                                    borderRadius: '10px',
                                    textDecoration: 'none',
                                    color: 'inherit',
                                }}
                            >
                                {post.profile && (
                                    <img
                                        src={`http://localhost:7777/uploads/${post.profile}`}
                                        alt={post.file}
                                        style={{ width: '50px', height: '50px', borderRadius: '50%' }}
                                    />
                                )}
                                {!post.profile && (
                                    <img
                                        src={`http://localhost:7777/uploads/noimage.png`}
                                        alt={post.file}
                                        style={{ width: '50px', height: '50px', borderRadius: '50%' }}
                                    />
                                )}
                                <h5 className="mb-0 ms-3">{post.name}</h5>
                            </Link>
                            <small className="text-muted">Posted on {formatDate(post.wdate)}</small>
                            <p className="ms-auto mb-0">{post.readNum} views</p>
                        </div>

                        <p className="mt-3 mb-3">{post.content}</p>
                        {post.fileName && post.fileName !== 'noimage.PNG' && (
                            <img
                                src={`http://localhost:7777/uploads/${post.fileName}`}
                                alt={post.file}
                                style={{ width: '90%', borderRadius: '1em' }}
                            />
                        )}
                    </>
                )}
            </div>

            <hr style={{ width: '600px', margin: '0 auto' }} />
            {Array.isArray(post.comments) &&
                post.comments
                    .slice()
                    .sort((a, b) => new Date(a.wdate) - new Date(b.wdate))
                    .map((comment) => (
                        <div
                            key={comment.id}
                            style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}
                        >
                            <div className="d-flex justify-content-between">
                                <div>
                                    <div className="d-flex align-items-center gap-3">
                                        <Link
                                            className="d-flex align-items-center"
                                            to={`/profile/${comment.userName}`}
                                            style={{
                                                borderRadius: '10px',
                                                textDecoration: 'none',
                                                color: 'inherit',
                                            }}
                                        >
                                            <img
                                                src={`http://localhost:7777/uploads/${comment.fileName}`}
                                                alt={post.file}
                                                style={{ width: '30px', height: '30px', borderRadius: '50%' }}
                                            />
                                            <h6 className="mb-0 ms-3">{comment.userName}</h6>
                                        </Link>

                                        <small className="text-muted">Posted on {formatDate(comment.wdate)}</small>
                                    </div>
                                </div>
                                {user && user.name == comment.userName && (
                                    <button
                                        className="btn btn-outline-danger btn-sm"
                                        onClick={() => deleteComment(comment.id)}
                                    >
                                        Delete
                                    </button>
                                )}
                            </div>
                            <p>{comment.content}</p>
                        </div>
                    ))}
            <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                <h5>댓글 작성</h5>
                <textarea
                    className="form-control my-2"
                    rows="3"
                    placeholder="댓글을 입력하세요..."
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                />
                <div className="d-flex">
                    <button
                        className="btn btn-primary ms-auto"
                        onClick={handleCommentSubmit}
                    >
                        댓글 작성
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Post;

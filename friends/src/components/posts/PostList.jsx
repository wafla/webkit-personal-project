import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { format } from 'date-fns';

import { FaRegComment } from 'react-icons/fa';

const PostList = ({ posts }) => {
    const [postList, setPostList] = useState(posts || []);
    const [page, setPage] = useState(0); // 현재 페이지 0부터 시작
    const [size, setSize] = useState(7); // 한 페이지 당 보여줄 목록 개수
    const [totalPages, setTotalPages] = useState(0); // 총 페이지 수
    const [totalCount, setTotalCount] = useState(0); // 총 게시글 수
    const navigate = useNavigate();

    const formatDate = (dateString) => {
        return format(new Date(dateString), 'yyyy-MM-dd HH:mm:ss');
    };

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

    useEffect(() => {
        fetchPostList();
    }, [posts, page]);

    const fetchPostList = async () => {
        let url = `http://localhost:7777/api/posts`;
        try {
            const response = await axios.get(url, {
                params: { page, size },
            });
            const fetchedPosts = response.data.data;
            setTotalCount(response.data.totalCount);
            setTotalPages(response.data.totalPages);

            const postsWithUserProfile = await Promise.all(
                fetchedPosts.map(async (post) => {
                    const profile = await fetchUserProfile(post.name);
                    console.log(post);

                    return { ...post, profile }; // Add profilePic to each post
                })
            );
            setPostList(postsWithUserProfile);
        } catch (error) {
            alert('Error: ' + error.message);
        }
    };

    const blockSize = 7;
    const currentBlock = Math.floor(page / blockSize);
    const startPage = currentBlock * blockSize;
    const endPage = Math.min(startPage + blockSize, totalPages);
    console.log(startPage, totalPages);

    return (
        <div
            className="post-list"
            style={{ maxWidth: '600px', margin: '0 auto' }}
        >
            {postList.length > 0 &&
                postList.map((post) => (
                    <Link
                        key={post.id}
                        className="d-block my-3 p-3"
                        to={`/post/${post.id}`}
                        style={{
                            backgroundColor: '#efefef',
                            borderRadius: '10px',
                            textDecoration: 'none',
                            color: 'inherit',
                        }}
                    >
                        <div className="d-flex">
                            <div className="flex-grow-1 ms-3">
                                <div>
                                    <div className="d-flex align-items-center gap-3">
                                        {post.profile && (
                                            <img
                                                src={`http://localhost:7777/uploads/${post.profile}`}
                                                alt={post.name}
                                                style={{ width: '40px', height: '40px', borderRadius: '50%' }}
                                            />
                                        )}
                                        {post.profile == null && (
                                            <img
                                                src={`http://localhost:7777/uploads/noimage.png`}
                                                alt={post.name}
                                                style={{ width: '40px', height: '40px', borderRadius: '50%' }}
                                            />
                                        )}
                                        <h5 className="mb-0">
                                            {post.name}{' '}
                                            <small className="text-muted">Posted on {formatDate(post.wdate)}</small>
                                        </h5>
                                    </div>
                                </div>
                                <h2>{post.title}</h2>
                                <p>{post.content}</p>
                            </div>
                            <div
                                className="flex-shrink-0"
                                style={{ width: '25%' }}
                            >
                                {post.fileName && post.fileName !== 'noimage.PNG' && (
                                    <img
                                        src={`http://localhost:7777/uploads/${post.fileName}`}
                                        alt={post.file}
                                        style={{ width: '90%', borderRadius: '1em' }}
                                    />
                                )}
                            </div>
                        </div>
                        <div className="d-flex align-items-center justify-content-between gap-2">
                            <div className="d-flex align-items-center gap-2">
                                <FaRegComment />
                                <h6 className="mb-0">{post.comments?.length || 0}</h6>
                            </div>
                            <p className="mb-0">{post.readNum} views</p>
                        </div>
                    </Link>
                ))}
            <div className="text-center">
                {/* 이전 블럭 */}
                {startPage > 0 && (
                    <button
                        className="btn btn-outline-primary"
                        onClick={() => setPage(startPage - 1)}
                    >
                        Prev
                    </button>
                )}
                {/* 페이지 번호 */}
                {Array.from({ length: endPage - startPage }, (_, i) => (
                    <button
                        className="btn btn-outline-primary"
                        key={startPage + i}
                        onClick={() => setPage(startPage + i)}
                    >
                        {startPage + i + 1}
                    </button>
                ))}
                {endPage < totalPages && (
                    <button
                        className="btn btn-outline-primary"
                        onClick={() => setPage(endPage)}
                    >
                        Next
                    </button>
                )}
            </div>
        </div>
    );
};

export default PostList;

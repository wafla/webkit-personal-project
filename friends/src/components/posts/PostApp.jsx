import React, { useState } from 'react';
import PostList from './PostList';
import postsData from './data';
// import PostForm from './PostForm';

export default function PostApp() {
    const [postList, setPostList] = useState(postsData);

    const addPostHandler = (newPost) => {
        setPostList([newPost, ...postList]);
    };

    return (
        <div className="container">
            <div className="row my-2">
                <div className="col-md-8 offset-md-2 col-sm-10 offset-sm-1">
                    <PostList posts={postList} />
                </div>
            </div>
        </div>
    );
}

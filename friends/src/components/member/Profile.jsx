import { useState, useContext, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AuthContext } from './AuthContext';
import axios from 'axios';
import { format } from 'date-fns';

import { IoSchool } from 'react-icons/io5';
import { FaBirthdayCake } from 'react-icons/fa';
import { FaGlobe } from 'react-icons/fa';
import { PiGenderIntersexBold } from 'react-icons/pi';
import { MdHotelClass } from 'react-icons/md';

function Profile() {
    const { nickname } = useParams();
    const { user, logoutUser } = useContext(AuthContext);
    // if (user) console.log('username: ' + user.name);

    const [user_, setUser_] = useState({
        nickname: null,
        school: null,
        profile_image: null,
        about_me: null,
        birthday: null,
        country: null,
        gender: null,
        hobby: null,
        indate: null,
    });
    const [isEditing, setIsEditing] = useState(false);
    const [editedUser, setEditedUser] = useState({ ...user_ });
    const [newProfileImage, setNewProfileImage] = useState(null);

    const navigate = useNavigate();

    const formatDate = (dateString) => {
        return format(new Date(dateString), 'yyyy-MM-dd');
    };

    const fetchUser = async (name) => {
        try {
            let response = await axios.get(`http://localhost:7777/api/users/${name}`);
            response = response.data.data;
            console.log(response);

            setUser_({
                nickname: response.name,
                school: response.school,
                profile_image: response.fileName,
                about_me: response.aboutMe,
                birthday: response.birthDay,
                country: response.country,
                gender: response.gender,
                hobby: response.hobby,
                indate: formatDate(response.indate),
            });
            setEditedUser({
                nickname: response.name,
                school: response.school,
                profile_image: response.fileName,
                about_me: response.aboutMe,
                birthday: response.birthDay,
                country: response.country,
                gender: response.gender,
                hobby: response.hobby,
                indate: formatDate(response.indate),
            });
        } catch (error) {
            alert('해당 유저가 없습니다.');
        }
    };

    useEffect(() => {
        fetchUser(nickname);
    }, [nickname]);

    const handleEditClick = () => {
        setIsEditing(true);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setEditedUser((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleProfileImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setNewProfileImage(file);
        }
    };

    const handleSaveClick = async () => {
        try {
            let formData = new FormData();

            const requestPayload = {
                school: editedUser.school,
                birthday: editedUser.birthday,
                country: editedUser.country,
                gender: editedUser.gender,
                hobby: editedUser.hobby,
                about_me: editedUser.about_me,
            };

            formData.append('request', new Blob([JSON.stringify(requestPayload)], { type: 'application/json' }));
            if (newProfileImage) {
                formData.append('file', newProfileImage);
            } else {
                console.log('사진 선택 안됨');
            }

            await axios.put(`http://localhost:7777/api/users/${nickname}`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            setUser_(editedUser);
            setIsEditing(false);
            fetchUser(nickname);
            alert('수정 됐습니다!');
        } catch (error) {
            console.error('Error updating profile:', error);
        }
    };

    const handleCancelClick = () => {
        setIsEditing(false);
        setEditedUser({ ...user_ });
        setNewProfileImage(null); // Reset the selected image
    };

    const deleteAccount = async () => {
        const confirmDelete = window.confirm('정말 탈퇴하시겠습니까?');
        if (!confirmDelete) return;
        try {
            const response = await axios.delete(`http://localhost:7777/api/users/${user.id}`);
            if (response.data.result == 'success') {
                logoutUser();
                sessionStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                localStorage.removeItem('user');
                alert('탈퇴 되었습니다.');
            } else {
                alert('오류가 발생했습니다.');
            }
        } catch (error) {
            alert('오류가 발생했습니다.');
        }
    };

    return (
        <div className="container">
            <div style={{ maxWidth: '600px', margin: '0 auto', padding: '20px' }}>
                <div className="d-flex align-items-center gap-1">
                    {user_.profile_image != null ? (
                        <img
                            src={`http://localhost:7777/uploads/${user_.profile_image}`}
                            alt={user_.profile_image}
                            style={{ width: '100px', height: '100px', borderRadius: '50%' }}
                        />
                    ) : (
                        <img
                            src={`http://localhost:7777/uploads/noimage.png`}
                            alt="No Image"
                            style={{ width: '100px', height: '100px', borderRadius: '50%' }}
                        />
                    )}
                    <div className="ms-3">
                        <div className="d-flex justify-content-center align-items-center gap-3">
                            <h1>{nickname}</h1>
                            <h6>Indate : {user_.indate}</h6>
                        </div>

                        {isEditing ? (
                            <>
                                <input
                                    type="text"
                                    name="school"
                                    value={editedUser.school}
                                    onChange={handleChange}
                                    placeholder="School"
                                    style={{ display: 'block', width: '100%', marginBottom: '10px' }}
                                />
                                <input
                                    type="date"
                                    name="birthday"
                                    value={editedUser.birthday}
                                    onChange={handleChange}
                                    placeholder="Birthday"
                                    style={{ display: 'block', width: '100%', marginBottom: '10px' }}
                                />
                                <input
                                    type="text"
                                    name="country"
                                    value={editedUser.country}
                                    onChange={handleChange}
                                    placeholder="Country"
                                    style={{ display: 'block', width: '100%', marginBottom: '10px' }}
                                />
                                <div className="mt-2">
                                    <label>
                                        <input
                                            type="radio"
                                            name="gender"
                                            value="MALE"
                                            checked={editedUser.gender === 'MALE'}
                                            onChange={handleChange}
                                        />
                                        MALE
                                    </label>
                                    <label className="ms-3">
                                        <input
                                            type="radio"
                                            name="gender"
                                            value="FEMALE"
                                            checked={editedUser.gender === 'FEMALE'}
                                            onChange={handleChange}
                                        />
                                        FEMALE
                                    </label>
                                </div>
                                <input
                                    type="text"
                                    name="hobby"
                                    value={editedUser.hobby}
                                    onChange={handleChange}
                                    placeholder="Hobby"
                                    style={{ display: 'block', width: '100%', marginBottom: '10px' }}
                                />
                                <textarea
                                    name="about_me"
                                    value={editedUser.about_me}
                                    onChange={handleChange}
                                    placeholder="About Me"
                                    style={{ display: 'block', width: '100%', marginBottom: '10px', height: '100px' }}
                                />
                                <input
                                    type="file"
                                    onChange={handleProfileImageChange}
                                    accept="image/*"
                                    style={{ display: 'block', marginBottom: '10px' }}
                                />
                                <button
                                    onClick={handleSaveClick}
                                    className="btn btn-outline-primary ms-auto"
                                >
                                    Save
                                </button>
                                <button
                                    onClick={handleCancelClick}
                                    className="btn btn-outline-danger ms-2"
                                >
                                    Cancel
                                </button>
                            </>
                        ) : (
                            <>
                                {user_.school && (
                                    <h6 className="mb-0 mt-1 d-flex align-items-center gap-1">
                                        <IoSchool />
                                        <small className="text-muted">{user_.school}</small>
                                    </h6>
                                )}
                                {user_.birthday && (
                                    <h6 className="mb-0 mt-1 d-flex align-items-center gap-1">
                                        <FaBirthdayCake />
                                        <small className="text-muted">{user_.birthday}</small>
                                    </h6>
                                )}
                                {user_.country && (
                                    <h6 className="mb-0 mt-1 d-flex align-items-center gap-1">
                                        <FaGlobe />
                                        <small className="text-muted">{user_.country}</small>
                                    </h6>
                                )}
                                {user_.gender && (
                                    <h6 className="mb-0 mt-1 d-flex align-items-center gap-1">
                                        <PiGenderIntersexBold />
                                        <small className="text-muted">{user_.gender}</small>
                                    </h6>
                                )}
                                {user_.hobby && (
                                    <h6 className="mb-0 mt-1 d-flex align-items-center gap-1">
                                        <MdHotelClass />
                                        <small className="text-muted">{user_.hobby}</small>
                                    </h6>
                                )}
                            </>
                        )}
                    </div>
                    {user && user.name === user_.nickname && !isEditing && (
                        <button
                            onClick={handleEditClick}
                            className="btn btn-outline-primary ms-auto"
                        >
                            Edit
                        </button>
                    )}
                    {user && user.name !== user_.nickname && (
                        <button
                            onClick={() => navigate(`/message/${user_.nickname}`)}
                            className="btn btn-outline-success ms-auto"
                        >
                            Message
                        </button>
                    )}
                </div>
                {!isEditing && <p style={{ padding: '20px' }}>{user_.about_me}</p>}
                <div className="d-flex">
                    {user && user.name === user_.nickname && (
                        <button
                            onClick={() => deleteAccount()}
                            className="btn btn-outline-danger ms-auto"
                        >
                            Delete Account
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Profile;

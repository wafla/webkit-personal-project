import axios from 'axios';
import axiosInstance from './axiosInstance';

export const checkTokenExpiration = (token) => {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expTime = payload.exp * 1000;
    let isInValid = expTime < Date.now();
    return isInValid;
};

export const refreshAccessToken = async () => {
    const refreshToken = localStorage.getItem('refreshToken');
    if (refreshToken) {
        let url = `http://localhost:7777/api/auth/refresh`;
        try {
            const response = await axiosInstance.post(url, { refreshToken });
            const newAccessToken = response.data;
            return newAccessToken;
        } catch (error) {
            console.error('refresh token error: ', error);
            return null;
        }
    }
    console.log('refreshToken 없음');
    return null;
};

export const getAuthenticUserInfo = () => {
    let url = `http://localhost:7777/api/auth/user`;
    const accessToken = sessionStorage.getItem('accessToken');
    if (accessToken) {
        axios
            .get(url, {
                header: {
                    Authorization: `Bearer ${accessToken}`,
                },
            })
            .then((response) => {
                const { id, email, name } = response.data;
                const authUser = { id, name, email };
                console.log('authUser: ', authUser);

                return authUser;
            })
            .catch((error) => {
                alert('error [getAuthenticUserInfo 에서 에러] ' + error);
                console.error(error);
                sessionStorage.removeItem('accessToken');
                return null;
            });
    }
};

import axios from 'axios';
import { checkTokenExpiration, refreshAccessToken } from './authUtil';

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use(
    async (config) => {
        const accessToken = sessionStorage.getItem('accessToken');
        console.log('요청 인터셉터 실행중 ... accessToken: ' + accessToken);
        if (accessToken) {
            if (checkTokenExpiration(accessToken)) {
                console.log('요청 인터셉터...accessToken 유효시간 지난 경우...');
                try {
                    const newAccessToken = await refreshAccessToken();
                    console.log('새 엑세스토큰 발급 받음');

                    if (newAccessToken) {
                        sessionStorage.setItem('accessToken', newAccessToken);
                        config.headers['Authorization'] = `Bearer ${newAccessToken}`;
                        return config;
                    }
                } catch (error) {
                    console.log('토큰 갱신 실패, 자동 로그아웃 실행');
                    sessionStorage.removeItem('accessToken');
                    localStorage.removeItem('refreshToken');
                    localStorage.removeItem('user');
                    window.location.href = '/'; // 자동 로그아웃
                    return Promise.reject(error);
                }
            }
            config.headers['Authorization'] = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const status = error.response?.status;
        console.log('응답 인터셉터에서 받은 응답 상태코드(status): ', status);
        if (status === 400) {
            alert(error.response.data.message); // 로그인을 하세요
            window.location.href = '/';
            return Promise.reject(error);
        }

        if (status === 401) {
            const refreshToken = localStorage.getItem('refreshToken');
            if (refreshToken) {
                try {
                    const newAccessToken = await refreshAccessToken();
                    if (newAccessToken) {
                        sessionStorage.setItem('accessToken', newAccessToken);
                        error.config.headers['Authorization'] = `Bearer ${newAccessToken}`;
                        return axiosInstance(error.config);
                    }
                } catch (err) {
                    console.log('리프레시 토큰도 만료됨, 로그아웃 처리');
                    localStorage.removeItem('refreshToken');
                    localStorage.removeItem('user');
                    sessionStorage.removeItem('accessToken');
                    window.location.href = '/'; // 자동 로그아웃
                    return Promise.reject(error);
                }
            }
            console.log('리프레시 토큰 없음, 로그아웃 처리');
            localStorage.removeItem('refreshToken');
            sessionStorage.removeItem('accessToken');
            localStorage.removeItem('user');
            window.location.href = '/';
            return Promise.reject(error);
        }
        if (status === 403) {
            alert('접근 권한이 없습니다');
            window.location.href = '/';
            return Promise.reject(error);
        }
    }
);

export default axiosInstance;

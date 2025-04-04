import { useState, useEffect, createContext } from 'react';

export const AuthContext = createContext({ id: '', name: '', email: '' });

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    const loginAuthUser = (userInfo) => {
        console.log(userInfo.name);
        setUser(userInfo);
    };

    const logoutUser = () => {
        setUser(null);
    };

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
    }, []);

    return <AuthContext.Provider value={{ user, loginAuthUser, logoutUser }}>{children}</AuthContext.Provider>;
};

// SignUp.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function SignUp() {
    const navigate = useNavigate();

    const [user, setUser] = useState({ name: '', email: '', passwd: '' });
    const [agree, setAgree] = useState('');
    const [pwdChk, setPwdChk] = useState('');
    // 에러 관련 state
    const [nameError, setNameError] = useState(false);
    const [passwdError, setPasswdError] = useState(false);
    const [emailError, setEmailError] = useState(false);
    const [agreeError, setAgreeError] = useState(false);
    const [passwdChkError, setPasswdChkError] = useState(false);
    const [nameDuplexError, setNameDuplexError] = useState(true); // 닉네임 중복체크
    const [emailDuplexError, setEmailDuplexError] = useState(true); //이메일 중복체크 결과에 따라 에러 출력시 사용

    //구조분해 할당
    const { name, email, passwd } = user;

    const onChange = (e) => {
        console.log(e.target.name);

        setUser({ ...user, [e.target.name]: e.target.value });
        if (e.target.name === 'name') setNameError(false);
        if (e.target.name === 'email') setEmailError(false);
        if (e.target.name === 'passwd') setPasswdError(false);
    };
    const onCheckEmail = (e) => {
        //이메일 중복체크
        isDuplicatedEmail();
    };
    const isDuplicatedName = async () => {
        try {
            setNameDuplexError(true);
            const response = await axios.post(`http://localhost:7777/api/users/check-name`, { name });
            if (name == '') {
                alert('닉네임을 입력하세요');
                return;
            }
            const data = response.data;
            if (data.result === 'ok') {
                //중복되지 않을 경우
                alert(data.message);
                setNameDuplexError(false);
            } else {
                //중복 이름일 경우
                alert(data.message);
                setUser({ ...user, name: '' });
                setNameDuplexError(true);
            }
        } catch (error) {
            alert('Error: ' + error);
            setNameDuplexError(true);
        }
    };
    const isDuplicatedEmail = async () => {
        try {
            setEmailDuplexError(true);
            const response = await axios.post(`http://localhost:7777/api/users/check-email`, { email });
            if (email == '') {
                alert('이메일을 입력하세요');
                return;
            }
            const data = response.data;
            if (data.result === 'ok') {
                //중복되지 않을 경우
                alert(data.message);
                setEmailDuplexError(false);
            } else {
                //중복 이메일일 경우
                alert(data.message);
                setUser({ ...user, email: '' });
                setEmailDuplexError(true);
            }
        } catch (error) {
            alert('Error: ' + error);
            setEmailDuplexError(true);
        }
    };

    const onCheckName = (e) => {
        isDuplicatedName();
    };

    const onChangePasswdChk = (e) => {
        setPasswdChkError(e.target.value !== passwd);
        setPwdChk(e.target.value);
    };

    const onChangeAgree = (e) => {
        setAgree(e.target.checked); //체크하면  true, 아니면 false
        //setAgreeError(!e.target.checked);
        if (e.target.checked) {
            setAgreeError(false);
        }
    };

    const onSubmit = (e) => {
        //서버 페이지(/input)로 전송되는 기본 동작을 막자==>ajax로 비동기 요청을 보낼 예정
        e.preventDefault();
        //유효성 체크
        if (!name) {
            setNameError(true);
            return;
        }
        if (!passwd) {
            setPasswdError(true);
            return;
        }
        //비밀번호와 비밀번호 확인 입력값 체크
        if (passwd !== pwdChk) {
            setPasswdChkError(true);
            return;
        }

        if (!email) {
            setEmailError(true);
            return;
        }

        if (!agree) {
            setAgreeError(true);
            return;
        }
        if (nameDuplexError) {
            alert('닉네임 중복 여부를 체크하세요');
            return;
        }
        //이메일 중복여부 체크
        if (emailDuplexError) {
            alert('이메일 중복 여부를 체크하세요');
            return;
        }

        //백엔드쪽에 회원가입 요청 보내기
        requestSignUp();
    };
    const onReset = () => {
        setUser({ name: '', email: '', passwd: '' });
        setAgree(false);
        setPwdChk('');
    };

    const requestSignUp = async () => {
        let url = `http://localhost:7777/api/users`;
        try {
            const response = await axios.post(url, { ...user, role: 'USER' });
            //alert(JSON.stringify(response.data));
            const { result, message } = response.data;
            if (result === 'success') {
                alert(message + '- 로그인 하세요');
                navigate('/'); //홈으로 페이지 이동
            } else {
                alert('회원 가입 실패-다시 시도하세요 ');
                onReset();
            }
        } catch (err) {
            if (err.response && (err.response.status === 500 || err.response.status === 400)) {
                alert('이메일 중복체크를 해야해요');
                setEmailDuplexError(true);
            } else {
                alert('Error: ' + err);
            }
        }
    };

    return (
        <div className="container py-2">
            <h1 className="text-dark text-center my-4">Signup</h1>
            <form
                action="/input"
                method="post"
                onSubmit={onSubmit}
            >
                <div className="row my-4">
                    <div className="col-md-8 offset-md-2">
                        <label htmlFor="userid">닉네임:</label>
                        <input
                            type="text"
                            name="name"
                            id="name"
                            onChange={onChange}
                            value={name}
                            placeholder="User Name"
                            className="form-control"
                        ></input>
                        <button
                            className="btn btn-outline-success"
                            type="button"
                            onClick={onCheckName}
                        >
                            중복체크
                        </button>
                        {nameError && <span className="text-danger">이름을 입력해야 해요</span>}
                    </div>
                </div>
                <div className="row my-4">
                    <div className="col-md-8 offset-md-2">
                        <label htmlFor="userid">Email:</label>
                        <input
                            type="text"
                            name="email"
                            id="email"
                            onChange={onChange}
                            value={email}
                            placeholder="Email"
                            className="form-control"
                        ></input>

                        <button
                            className="btn btn-outline-success"
                            type="button"
                            onClick={onCheckEmail}
                        >
                            중복체크
                        </button>
                        {emailError && <span className="text-danger">Email을 입력해야 해요</span>}
                    </div>
                </div>
                <div className="row my-4">
                    <div className="col-md-8 offset-md-2">
                        <label htmlFor="userid">비밀번호:</label>
                        <input
                            type="password"
                            name="passwd"
                            id="passwd"
                            onChange={onChange}
                            value={passwd}
                            placeholder="Password"
                            className="form-control"
                        ></input>
                        {passwdError && <span className="text-danger">비밀번호를 입력해야 해요</span>}
                    </div>
                </div>
                <div className="row my-4">
                    <div className="col-md-8 offset-md-2">
                        <label htmlFor="userid">비밀번호 확인:</label>
                        <input
                            type="password"
                            name="pwdChk"
                            id="pwdChk"
                            value={pwdChk}
                            onChange={onChangePasswdChk}
                            placeholder="Re Password"
                            className="form-control"
                        ></input>
                        {passwdChkError && <span className="text-danger">비밀번호가 일치하지 않아요</span>}
                    </div>
                </div>
                <div className="row my-4">
                    <div className="col-md-8 offset-md-2">
                        <label>
                            <input
                                type="checkbox"
                                name="agree"
                                id="agree"
                                onChange={onChangeAgree}
                            />
                            이용약관에 동의합니다.
                        </label>
                        {agreeError && <div className="text-danger">이용약관 동의에 체크해야 해요</div>}
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-8 offset-md-2 text-center">
                        <button className="btn btn-outline-primary mx-2">회원가입</button>
                        <button
                            type="reset"
                            onClick={onReset}
                            className="btn btn-outline-danger"
                        >
                            다시쓰기
                        </button>
                    </div>
                </div>
            </form>
        </div>
    );
}

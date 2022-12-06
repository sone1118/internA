let timer;
let isRunning = false;

const offModal1 = () => {
	document.querySelector(".authenticate_bg1").style.display = "none";
};

const offModal2 = () => {
	document.querySelector(".authenticate_bg2").style.display = "none";
	clearInterval(timer);
	document.querySelector('#timer').textContent = "";
};

const onModal1 = () => {
	document.querySelector(".authenticate_bg1").style.display = "block";
	document.querySelector(".input_text1").focus();
	document.querySelector(".authenticate_close1").addEventListener("click", offModal1);	
};

const onModal2 = () => {
	offModal1();
	document.querySelector(".authenticate_bg2").style.display = "block";
	document.querySelector(".input_text2").focus();
	document.querySelector(".authenticate_close2").addEventListener("click", offModal2);	
};
 
const startTimer = (count, display) => {
        let minutes, seconds;
        timer = setInterval(function () {
        minutes = parseInt(count / 60, 10);
        seconds = parseInt(count % 60, 10);
 
        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;
 
        display.textContent = minutes + ":" + seconds;
 
        if (--count < 0) { //타이머 종료
	     clearInterval(timer);
	     display.textContent = "";
	     isRunning = false;
	     
	     alert("시간이 초과 되었습니다.");
	     offModal2();
        }
    }, 1000);
};

//이메일 형태로 가는지 확인할 필요가 있을 것 같다.
const sendEmail = (e) => {
	e.preventDefault(); //새로고침 방지

	console.log("메일에 인증 번호를 전송합니다");	
	 fetch(e.target.action, { //이메일 보내기
        method: 'POST',
        body: new URLSearchParams(new FormData(e.target))
    })
    .then((response) => {	
		if(!response.ok) throw Error(); //에러던지기
		console.log("이메일 전송에 성공했습니다.");
		console.log("response status: ", response.status);
        
		e.target.reset(); //input 값 전부 비우기
		onModal2(); //인증 번호 입력창 보여주기
		
		let leftSec = 180; // timer 설정
		const display = document.querySelector('#timer');
		
		if (isRunning) clearInterval(timer); // 타이머가 이미 작동중이면 중지
		else isRunning = true;
		
		startTimer(leftSec, display); //타이머 시작
		document.querySelector("#error_message").innerText = ""; //에러 메세지가 있으면 지우기  
    })
    .catch((error) => { //메일 전송에 실패 했을 경우
        console.log("이메일 전송에 실패했습니다. error: ", error);
        alert("이메일 전송에 실패했습니다.");
        offModal1();
    });
};

const sendNumber = (e) => {
	e.preventDefault(); //새로고침 방지
	
	console.log("인증번호를 보냅니다."); 
	
	fetch(e.target.action, { //인증번호 보내기
        method: 'POST',
        body: new URLSearchParams(new FormData(e.target))
    })
    .then((response) => {
		if(!response.ok) throw Error(); //에러 던지기
		
		console.log("인증 번호가 일치합니다.");
		console.log("response status: ", response.status);
		
		e.target.reset();
		alert("인증에 성공했습니다.");
		offModal2();
    })
    .catch((error) => { //인증번호 인증에 실패 했을 경우.
        console.log("인증 번호가 틀렸습니다. error: ", error);
		document.querySelector("#error_message").innerText = "인증 번호가 틀렸습니다.";
		e.target.reset();
		document.querySelector(".input_text2").focus();
    });
};

const authenticate = () => {
	const email_btn =  document.querySelector("#email_btn"); //이메일 모달창 버튼
	const email_form1 = document.querySelector("#email_form1");
	const email_form2 = document.querySelector("#email_form2");
	
	email_btn && email_btn.addEventListener("click", onModal1); //이메일 모달창 띄우기
	email_form1 && email_form1.addEventListener("submit", sendEmail); //이메일 보내기
	email_form2 && email_form2.addEventListener("submit", sendNumber); //인증번호 보내기
};

authenticate();
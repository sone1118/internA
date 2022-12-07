let timer;
let isRunning = false;
let randomNumber = 0;

const createRandomNumber = (min, max) => {
	return Math.floor(Math.random() * (max - min + 1)) + min;
};

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

const sendEmail = (e) => {
	e.preventDefault(); //새로고침 방지
      	
	e.target.reset(); //input 값 전부 비우기
	onModal2(); //인증 번호 입력창 보여주기
	
	let leftSec = 180; // timer 설정
	const display = document.querySelector('#timer');
	
	if (isRunning) clearInterval(timer); // 타이머가 이미 작동중이면 중지
	else isRunning = true;
	
	startTimer(leftSec, display); //타이머 시작
	document.querySelector("#error_message").innerText = ""; //에러 메세지가 있으면 지우기   
	randomNumber = createRandomNumber(10000, 99999); //핸드폰으로 랜덤값 인증 번호 보내기
	console.log("인증번호: ", randomNumber);
};

const sendNumber = (e) => {
	e.preventDefault(); //새로고침 방지
	
	console.log("인증번호를 보냅니다."); 
	
	if(document.querySelector(".input_text2").value == randomNumber) { //인증번호가 맞으면
		e.target.reset();
		alert("인증에 성공했습니다.");
		offModal2();
	}
	else { //인증번호가 틀리면.
		console.log("인증 번호가 틀렸습니다.");
		document.querySelector("#error_message").innerText = "인증 번호가 틀렸습니다.";
		e.target.reset();
		document.querySelector(".input_text2").focus();
	}
};

const authenticate = () => {
	const email_btn =  document.querySelector("#email_btn"); //이메일 모달창 버튼
	const email_form1 = document.querySelector("#email_form1"); //이메일 form
	const email_form2 = document.querySelector("#email_form2"); //인증번호 form
	
	email_btn && email_btn.addEventListener("click", onModal1); //이메일 모달창 띄우기
	email_form1 && email_form1.addEventListener("submit", sendEmail); //이메일 보내기
	email_form2 && email_form2.addEventListener("submit", sendNumber); //인증번호 보내기
};

authenticate();
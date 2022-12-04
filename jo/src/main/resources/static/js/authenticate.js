const offModal = () => {
	//document.querySelector(".authenticate_wrap").style.display = "none";
	document.querySelector(".authenticate_bg1").style.display = "none";
};

const onModal = () => {
	//document.querySelector(".authenticate_wrap").style.display = "block";
	document.querySelector(".authenticate_bg1").style.display = "block";
	document.querySelector(".input_text").focus();
	document.querySelector(".authenticate_close").addEventListener("click", offModal);	
};

const sendEmail = (e) => {
	e.preventDefault();
	//이메일 보내기
	
};
const sendPhone = (e) => {
	e.preventDefault();
	//휴대폰 보내기
};

const authenticate = () => {
	const phone_btn = document.querySelector("#phone_btn");
	const phone_form = document.querySelector("#phone_form");
	const email_btn = document.querySelector("#email_btn");
	const email_form = document.querySelector("#email_form");
	
	phone_btn && phone_btn.addEventListener("click", onModal);
	email_btn && email_btn.addEventListener("click", onModal);
	phone_form && phone_form.addEventListener("submit", sendPhone);
	email_form && email_form.addEventListener("submit", sendEmail);
};

authenticate();
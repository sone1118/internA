const withdrawalSubmit = () => {
	const deleteMethod = {
	 method: 'DELETE',
	};

	 fetch('/jo/api/users', deleteMethod)
    .then((response) => {
		if(!response.ok) throw Error(); //에러 던지기
		
		alert("GOOD BYE!!");
		window.location.href = 'http://localhost:8080/jo/';
    })
    .catch((error) => { //인증번호 인증에 실패 했을 경우.
        alert("탈퇴 실패");
        window.location.href = 'http://localhost:8080/jo/withdrawal';
    });
};

const withdrawalLast = () => {
	const confirm_btn = document.querySelector("#confirm_btn");
	confirm_btn.addEventListener("click", withdrawalSubmit);
};

withdrawalLast();
const withdrawalSubmit = () => {
	console.log("회원 탈퇴 api요청 DELETE"); 

	const deleteMethod = {
	 method: 'DELETE',
	};

	 fetch('/jo/api/users', deleteMethod)
    .then((response) => {
		console.log("response:" , response.status);
		if(!response.ok) throw Error(); //에러 던지기
		
		console.log("회원 탈퇴 성공!!");
		
		alert("GOOD BYE!!");
		window.location.href = 'http://localhost:8080/jo/';
    })
    .catch((error) => { //인증번호 인증에 실패 했을 경우.
        console.log("회원 탈퇴에 실패했습니다. : ", error);
        alert("탈퇴 실패");
        window.location.href = 'http://localhost:8080/jo/withdrawal';
    });
};

const withdrawalLast = () => {
	const confirm_btn = document.querySelector("#confirm_btn");
	confirm_btn.addEventListener("click", withdrawalSubmit);
};

withdrawalLast();
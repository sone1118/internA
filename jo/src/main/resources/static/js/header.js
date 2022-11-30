/*
const getData = function () {
var responseClone;
fetch("http://localhost:8080/jo/mypage", {
  	method: "POST",
  	headers: {
	"Content-Type": "application/json",
    "Authorization": "accesstoken",
  },
 })
  .then((response) => {
	   responseClone = response.clone();
	  return response.json()})
  .then((data) => console.log(data));
}
*/
/*
const getData = function() {
	const BASE_URL = 'http://localhost:8080/jo/mypage';
    console.log('BASE_URL', BASE_URL);
    fetch(BASE_URL, {
        method:'POST',
        headers: {
            'Accept': 'application/json',
            "Content-Type" : "application/json",
            "Authorization": "Bearer BQDyac2glKnbstiG79UKzKSReNbsWa_hEKlOWAZtXaFZpfx8ZibluRUmBHHO12CjLMJv3KBaKTZqUKJReA11_ItrYIkr3CmnUi6ykUD7J0gZk9pKzxjz02j4byQfSa6s7Y08OMNzugFffYc68tzZiGSDp9vB80eiiIod_igAH8ZxbPBUMsRH3pbiMY8tnJpeXmk"
            },
    })
    .then(response => response.json())
    .then(data => {
		console.log(data);
        const artist = json.artists.items[0];
        console.log('artist', artist);
        this.setState({artist});
    });
}
*/
/*
const getCookie = function (name) {
    var value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value ? decodeURIComponent(value[2]) : null;
};
*/
const getData = function() {
	const access = getCookie('access');
	fetch("http://localhost:8080/jo/mypage",{
                    method : 'POST',
                    mode : 'cors',
                    cache : 'no-cache',
                    /////Content Type은 json으로 명시한다.
                    headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + access },
                    credentials : 'same-origin',
                    redirect : 'follow',
                    referrer : 'no-referrer',
}).then(response => console.log(response));
};
//  body: JSON.stringify({
//    title: "Test",
//    body: "I am testing!",
//   userId: 1,
//  }),

if(getCookie("refresh")) {
	const header = document.querySelector(".header");
	const logout = document.querySelector("#logout");
	const mypage_btn = document.querySelector("#mypage_btn");
	if(header !== null) header.classList.remove("hidden");
	logout.addEventListener("click", onDelete);
	mypage_btn.addEventListener("click", getData);
}else {
	document.querySelector(".header").classList.add("hidden");
}
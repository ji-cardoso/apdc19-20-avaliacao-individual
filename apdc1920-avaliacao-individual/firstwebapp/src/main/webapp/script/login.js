captureData = function(event) {
	var data = $('form[name="login"]').jsonify();
	console.log(data);
	$.ajax({
		type: "POST",
		url: "https://mimetic-surf-271517.appspot.com/rest/login/v1",
		contentType: "application/json; charset=utf-8",
		crossDomain: true,
		success: function(response) {
			if(response) {
				console.log(response);
				localStorage.setItem('user_token', response.user_token);
				localStorage.setItem('user', response.user);
				if(response.user_role == "USER")
				location.replace("/user.html");
				if(response.user_role == "SU")
					location.replace("/SU.html");
				if (response.user_role == "GBO")
					location.replace("/GBO.html");
			}
			else {
				alert("No response");
			}
		},
		error: function(response) {
			if(response.status == 1){
				alert("Wrong password.");
			}
			else
				alert("Error: "+ response.status);
		},
		data: JSON.stringify(data)
	});

	event.preventDefault();
};

window.onload = function() {
	var frms = $('form[name="login"]');   
	frms[0].onsubmit = captureData;
}
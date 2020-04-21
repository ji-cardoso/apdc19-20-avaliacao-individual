captureData = function(event) {
    var data = $('form[name="update-form"]').jsonify();
    data.user_token = localStorage.getItem('user_token');
    data.user = localStorage.getItem('user');

    console.log(data);
    $.ajax({
        type: "PUT",
        url: "https://mimetic-surf-271517.appspot.com/rest/update/v1",
        contentType: "application/json; charset=utf-8",
        headers: {"X-HTTP-Method-Override": "PUT"}, // X-HTTP-Method-Override set to PUT.
        crossDomain: true,
        success: function(response) {
                alert("Saved modifications");
                localStorage.setItem('user_token', response.user_token);
				localStorage.setItem('user', response.user);
				if(response.user_role == "USER")
					location.replace("/user.html");
				if (response.user_role == "GBO")
					location.replace("/GBO.html");

        },
        error: function(response) {
            alert("Error: "+ response.status);
        },
        data: JSON.stringify(data)
    });

    event.preventDefault();
};

window.onload = function() {
    var frms = $('form[name="update-form"]');
    frms[0].onsubmit = captureData;
}
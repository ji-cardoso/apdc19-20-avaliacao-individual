captureData = function(event) {
    var data = $('form[name="register-form"]').jsonify();
    console.log(data);
    $.ajax({
        type: "POST",
        url: "https://mimetic-surf-271517.appspot.com/rest/register/user",
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        success: function(response) {
                alert("User registered ");
                window.location.href = "/login.html";

        },
        error: function(response) {
        	if(response.status == 1){
           	 	alert("Error: Invalid post code. A valid post code looks like XXXX-XXX");

        	}
        	else{
        	 alert("Error: "+ response.status);
        	}
           
        },
        data: JSON.stringify(data)
    });

    event.preventDefault();
};

window.onload = function() {
    var frms = $('form[name="register-form"]');
    frms[0].onsubmit = captureData;
}
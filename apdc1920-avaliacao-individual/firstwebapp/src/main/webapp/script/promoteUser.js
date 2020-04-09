captureData = function(event) {
    var data = $('form[name="promoteUser"]').jsonify();
    data.user_token = localStorage.getItem('user_token');
    data.user = localStorage.getItem('user');

    console.log(data);
    $.ajax({
        type: "PUT",
        url: "https://mimetic-surf-271517.appspot.com/rest/promoteUser/user",
        contentType: "application/json; charset=utf-8",
        headers: {"X-HTTP-Method-Override": "PUT"}, // X-HTTP-Method-Override set to PUT.
        crossDomain: true,
        success: function(response) {
                alert("User was successfully promoted to the role of GBO.");
                window.location.href = "/SU.html";
        },
        error: function(response) {
            alert("Error: "+ response.status);
        },
        data: JSON.stringify(data)
    });

    event.preventDefault();
};

window.onload = function() {
    var frms = $('form[name="promoteUser"]');
    frms[0].onsubmit = captureData;
}

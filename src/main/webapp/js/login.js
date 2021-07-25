$("#login").change(function() {
    if ($('#login').val() != "") {

        $.ajax({
            url: "/final_task/validation/login_l",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({ login: $('#login').val() }),
            dataType: 'JSON',
            success: function (resp) {
                if (!resp.exists) {
                    warn("This login is not registered!");
                } else {
                    $('#login').css('border', '2px solid #74b0eb');
                }
            }
        });

    }
});


$('.form').submit(function(event) {
    event.preventDefault();

    let faults = $('input').filter(function() {

        return $(this).data('required') && $(this).val() === "";
    }).css("border-color", "crimson");

    if (faults.length) return false;

    submitForm();
});


function submitForm() {
    let up = JSON.stringify({

        login: $('#login').val(),
        password: $('#password').val()

    });

    $.ajax({
        url: "/final_task/login",
        type: "POST",
        contentType: 'application/json',
        data: up,
        dataType: 'JSON',
        success: function (resp) {
            if (resp.user) {
                console.log("success");
                document.location.href = "../final_task/todo.html";
            } else {
                console.log("Wrong login or password!");
                warn("Wrong login or password!");
            }
        }
    });

}


function warn(text) {
    $('#warn').append(`${text}`).animate({opacity: "show", top: '45%'}, "slow");
    setTimeout(function() { $('#warn').animate({opacity: "hide", top: '55%'}, "fast").empty(); }, 2000);
}
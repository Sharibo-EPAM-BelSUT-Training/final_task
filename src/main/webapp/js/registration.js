$("#email").change(function() {
    if ($('#email').val() != "") {

        $.ajax({
            url: "/final_task/validation/email",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({ email: $('#email').val().toLowerCase() }),
            dataType: 'JSON',
            success: function (resp) {
                if (resp.exists) {
                    warn("Email is already exist!");
                } else if (!resp.validation) {
                    warn("Not valid email!");
                } else {
                    $('#email').css('border', '2px solid #74b0eb');
                }
            }
        });

    }
});

$("#login").change(function() {
    if ($('#login').val() != "") {

        $.ajax({
            url: "/final_task/validation/login_r",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({ login: $('#login').val() }),
            dataType: 'JSON',
            success: function (resp) {
                if (resp.exists) {
                    warn("Login is already exist!");
                } else if (!resp.length) {
                    warn("Login must contain 6-30 characters!");
                } else if (!resp.validation) {
                    warn("Not valid login!");
                } else {
                    $('#login').css('border', '2px solid #74b0eb');
                }
            }
        });

    }
});

$("#password").change(function() {

    if ($('#password').val() != "") {

        $.ajax({
            url: "/final_task/validation/password",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({ password: $('#password').val() }),
            dataType: 'JSON',
            success: function (resp) {
                if (!resp.length) {
                    warn("Password must contain 8-30 characters!");
                } else if (!resp.validation) {
                    warn("Not valid password!");
                } else {
                    $('#password').css('border', '2px solid #74b0eb');
                }
            }
        });

    }
});

$("#password2").change(function() {

    if ($('#password2').val() != "") {

        $.ajax({
            url: "/final_task/validation/password",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({ password: $('#password2').val() }),
            dataType: 'JSON',
            success: function (resp) {
                if (!resp.length) {
                    warn("Password must contain 8-30 characters!");
                } else if (!resp.validation) {
                    warn("Not valid password!");
                } else {
                    $('#password2').css('border', '2px solid #74b0eb');
                }
            }
        });

    }
});


$('.form').submit(function(event) {
    event.preventDefault();

    let faults = $('input').filter(function() {

        return $(this).data('required') && $(this).val() === "";
    }).css('border', '2px solid crimson');

    if (faults.length) return false;

    if ( $('#password').val() !== "") {
        if ($('#password').val() === $('#password2').val()) {
            submitForm();
        } else {
            warn("Passwords are not equivalent!");
        }
    }
});

function submitForm() {
    let up = JSON.stringify({

        email: $('#email').val().toLowerCase(),
        login: $('#login').val(),
        password: $('#password').val(),
        password2: $('#password2').val()

    });

    $.ajax({
        url: "/final_task/registration",
        type: "POST",
        contentType: 'application/json',
        data: up,
        dataType: 'JSON',
        success: function (resp) {

            switch (resp.resp) {
                case 0:
                    console.log("success");
                    document.location.href = "../final_task/login.html";
                    break;
                case 1:
                    console.log("You have already registered!");
                    warn("You have already registered!");
                    break;
                case 2:
                    console.log("This email is already in use!");
                    warn("This email is already in use!");
                    break;
                case 3:
                    console.log("This login is already in use!");
                    warn("This login is already in use!");
                    break;
                case -1:
                    if (!resp.validEmail) {
                        warn("Not valid email!");
                    } else if (!resp.validLogin) {
                        warn("Not valid login!");
                    } else if (!resp.validPassword) {
                        warn("Not valid password!");
                    } else if (!resp.validPassword2) {
                        warn("Not valid password!");
                    } else if (!resp.passwordsEqual) {
                        warn("Passwords are not equivalent!");
                    } //TODO можно сделать кейсами без брейков, а потом выделять все неправильные поля красным.
            }
        }
    });

}

function warn(text) {
    $('#warn').append(`${text}`).animate({opacity: "show", top: '45%'}, "slow");
    setTimeout(function() { $('#warn').animate({opacity: "hide", top: '55%'}, "fast").empty(); }, 2000);
}
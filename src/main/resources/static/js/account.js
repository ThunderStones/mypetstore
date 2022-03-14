$(function () {
    $('#registerUsername').blur(() => {
        if ($('#registerUsername').val() === '') return;
        $.ajax({
            type : 'GET',
            url: 'usernameExist?username=' + $('#registerUsername').val(),
            beforeSend : () => {
                $("#usernameTips").removeAttr('class').val('');
            },
            success : (data) => {
                if (!data) {
                    $('#usernameTips').attr('class', 'okTips').text('Available').class('okTips');
                } else {
                    $('#usernameTips').attr('class', 'errorTips').text('This name has been used.').class('errorTips');
                }
            }
        });
    });
})
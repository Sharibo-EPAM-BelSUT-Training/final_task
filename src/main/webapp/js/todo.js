let list = 1;

let today_notes = [];
let tomorrow_notes = [];
let someday_notes = [];
let deleted_notes = [];

$.ajax({
    url: "/final_task/TODO/session",
    type: "POST",
    contentType: 'application/json',
    dataType: 'JSON',
    success: function (resp) {
        if (resp.status == true) {
            console.log("success");
        } else {
            document.location.href = "../final_task/login.html";
        }
    }
});

$('#up').ready(function() {
    $.ajax({
        url: "/final_task/TODO/getNotes",
        type: "POST",
        contentType: 'application/json',
        dataType: 'JSON',
        success: function (resp) {
            list = resp.list;

            if (resp[0] != null) {
                try {
                    let i = 0;
                    while (true) {
                        if (resp[i].status == 1) {
                            today_notes.push(resp[i]);
                        } else if (resp[i].status == 2) {
                            tomorrow_notes.push(resp[i]);
                        } else if (resp[i].status == 3) {
                            someday_notes.push(resp[i]);
                        } else if (resp[i].status == 4) {
                            deleted_notes.push(resp[i]);
                        }

                        i++;
                    }
                } catch (e) {
                    console.log(e);
                }

                switch (list.toString()) {
                    case "1":
                        for (let note of today_notes) {
                            createNoteBlock(note, today_notes.indexOf(note), 1);
                        }
                        break;
                    case "2":
                        for (let note of tomorrow_notes) {
                            createNoteBlock(note, tomorrow_notes.indexOf(note), 2);
                        }
                        break;
                    case "3":
                        for (let note of someday_notes) {
                            createNoteBlock(note, someday_notes.indexOf(note), 3);
                        }
                }

            }

            switch (list.toString()) {
                case "1":
                    $('#today_button').css('border', '3px solid #74b0eb');
                    break;
                case "2":
                    $('#tomorrow_button').css('border', '3px solid #74b0eb');
                    break;
                case "3":
                    $('#someday_button').css('border', '3px solid #74b0eb');
            }

        }

    });
});

function SendComment(e) {
    e = e || window.event;
    if (e.keyCode == 13 && e.ctrlKey) {
        createMessage();
    }
}

function getFileName (str) {
    let i;
    if (str.lastIndexOf('\\')) {
        i = str.lastIndexOf('\\') + 1;
    }
    else {
        i = str.lastIndexOf('/') + 1;
    }

    let filename = str.slice(i);
    let uploaded = document.getElementById("fileformlabel");
    uploaded.innerHTML = filename;
}

function createMessage() {
    const fd = new FormData();

    let file = document.getElementById('upload').files[0];
    if (file != null) {
        if (file.size <= 10485760) {
            fd.append("file", file, file.name);
        } else {
            alert("A maximum file size is 10 MB!")
            return;
        }
    }

    let up = JSON.stringify({
        text: $('#message_input').val(),
        status: list,
    });

    fd.append("up", up);

    if (file != null || $('#message_input').val() != "") {

        $.ajax({
            url: "/final_task/TODO/message",
            type: "POST",
            data: fd,
            processData: false,
            contentType: false,
            timeout: 10000,
            success: function (resp) {
                console.log(resp);
                let down = JSON.parse(resp);
                console.log(down);
                if (down != null) {
                    $('#message_input').val('').focus();
                    document.getElementById("fileformlabel").innerText = '';
                    $('#upload').val('');

                    switch (list.toString()) {
                        case "1":
                            today_notes.push(down.note);
                            createNoteBlock(down.note, today_notes.indexOf(down.note), 1);
                            break;
                        case "2":
                            tomorrow_notes.push(down.note);
                            createNoteBlock(down.note, tomorrow_notes.indexOf(down.note), 2);
                            break;
                        case "3":
                            someday_notes.push(down.note);
                            createNoteBlock(down.note, someday_notes.indexOf(down.note), 3);
                    }

                    console.log("success");
                } else {
                    console.log("fail"); //TODO пометить красным инпут?
                }
            }
        });

    }
}

function createNoteBlock(note, index, list) {

    if (note.note == "null") {
        $(`<div class="note_container_1" id="${note.id}"><div class="note_container_2" id="${list}">
<div class="note">
    <div class="note_file_container">
        <a href="usersFiles/${note.user_id}/${note.file_path}">
            <img id="img_note_file" src="images/File.png" width="25px" height="25px" alt="File">
        </a>
        <div class="note_file"><a href="usersFiles/${note.user_id}/${note.file_path}">${note.file_path.replace(/^[1-9]{4}/g, "")}</a></div>
        <div class="note_file_buttons">
            <button id="delete_file_button" onclick="deleteNoteFile(this.parentNode.parentNode.parentNode.parentNode.parentNode.getAttribute('id'),
                this.parentNode.parentNode.parentNode.parentNode.getAttribute('id'))">
                <img src="images/InTrash.png" width="16px" height="16px" alt="Delete file">
            </button>
        </div>
    </div>
</div>
<div class="note_buttons">
    <button class="ok_button" onclick="setNoteStatusOk(this.parentNode.parentNode.parentNode.getAttribute('id'),
        this.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Ok.png" width="20px" height="20px" alt="Ok">
    </button>
    <button class="inTrash_button" onclick="putNoteInTrash(this.parentNode.parentNode.parentNode.getAttribute('id'),
        this.parentNode.parentNode.getAttribute('id'))">
        <img src="images/InTrash.png" width="20px" height="20px" alt="Delete">
    </button>
</div>

</div>
</div>`).appendTo('#up').slideDown();

    } else if (note.file_path == "null") {
        note.note = note.note.replace(/#!/g, "<br/>");

        $(`<div class="note_container_1" id="${note.id}"><div class="note_container_2" id="${list}">
<div class="note">${note.note}</div>
<div class="note_buttons">
    <button class="ok_button" onclick="setNoteStatusOk(this.parentNode.parentNode.parentNode.getAttribute('id'),
        this.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Ok.png" width="20px" height="20px" alt="Ok">
    </button>
    <button class="inTrash_button" onclick="putNoteInTrash(this.parentNode.parentNode.parentNode.getAttribute('id'),
        this.parentNode.parentNode.getAttribute('id'))">
        <img src="images/InTrash.png" width="20px" height="20px" alt="Delete">
    </button>
</div>

</div>
</div>`).appendTo('#up').slideDown();

    } else {
        note.note = note.note.replace(/#!/g, "<br/>");

        $(`<div class="note_container_1" id="${note.id}"><div class="note_container_2" id="${list}">
<div class="note">${note.note}
    <div class="note_file_container">
        <a href="usersFiles/${note.user_id}/${note.file_path}">
            <img id="img_note_file" src="images/File.png" width="25px" height="25px" alt="File">
        </a>
        <div class="note_file"><a href="usersFiles/${note.user_id}/${note.file_path}">${note.file_path.replace(/^[1-9]{4}/g, "")}</a></div>
        <div class="note_file_buttons">
            <button id="delete_file_button" onclick="deleteNoteFile(this.parentNode.parentNode.parentNode.parentNode.parentNode.getAttribute('id'),
                this.parentNode.parentNode.parentNode.parentNode.getAttribute('id'))">
                <img src="images/InTrash.png" width="16px" height="16px" alt="Delete file">
            </button>
        </div>
    </div>
</div>
<div class="note_buttons">
    <button class="ok_button" onclick="setNoteStatusOk(this.parentNode.parentNode.parentNode.getAttribute('id'),
        this.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Ok.png" width="20px" height="20px" alt="Ok">
    </button>
    <button class="inTrash_button" onclick="putNoteInTrash(this.parentNode.parentNode.parentNode.getAttribute('id'),
        this.parentNode.parentNode.getAttribute('id'))">
        <img src="images/InTrash.png" width="20px" height="20px" alt="Delete">
    </button>
</div>

</div>
</div>`).appendTo('#up').slideDown();
    }

}

function getSomedayNotes() {
    if (list != 3) {
        $('#up').find('.note_container_1').remove();
        for (let note of someday_notes) {
            createNoteBlock(note, someday_notes.indexOf(note), 3);
        }

        $('#someday_button').css('border', '3px solid #74b0eb');
        $('#today_button').css('border', '1px solid #2265a8');
        $('#tomorrow_button').css('border', '1px solid #2265a8');

        list = 3;
        setList();
    }
}

function getTodayNotes() {
    if (list != 1) {
        $('#up').find('.note_container_1').remove();
        for (let note of today_notes) {
            createNoteBlock(note, today_notes.indexOf(note), 1);
        }

        $('#someday_button').css('border', '1px solid #2265a8');
        $('#today_button').css('border', '3px solid #74b0eb');
        $('#tomorrow_button').css('border', '1px solid #2265a8');

        list = 1;
        setList();
    }
}

function getTomorrowNotes() {
    if (list != 2) {
        $('#up').find('.note_container_1').remove();
        for (let note of tomorrow_notes) {
            createNoteBlock(note, tomorrow_notes.indexOf(note), 2);
        }

        $('#someday_button').css('border', '1px solid #2265a8');
        $('#today_button').css('border', '1px solid #2265a8');
        $('#tomorrow_button').css('border', '3px solid #74b0eb');

        list = 2;
        setList();
    }
}

function setList() {
    $.ajax({
        url: "/final_task/TODO/order",
        type: "POST",
        data: JSON.stringify({ list: list }),
        dataType: 'JSON',
        success: function (resp) {
            if (resp.list) {
                console.log("success");
            }
        }
    });
}

function setNoteStatusOk(index, list) {
    let up;
    let array_index;
    switch (list.toString()) {
        case "1":
            for (let note of today_notes) {
                if (note.id === index) {
                    array_index = today_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
            break;
        case "2":
            for (let note of tomorrow_notes) {
                if (note.id === index) {
                    array_index = tomorrow_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
            break;
        case "3":
            for (let note of someday_notes) {
                if (note.id === index) {
                    array_index = someday_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
    }

    $.ajax({
        url: "/final_task/TODO/statusOk",
        type: "POST",
        data: up,
        dataType: 'JSON',
        success: function (resp) {
            if (resp.status == true) {
                $(`#up .note_container_1[id="${index}"]`).hide( "slow" );
                setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).remove(); },600);
                switch (list.toString()) {
                    case "1":
                        today_notes.splice(array_index, 1);
                        break;
                    case "2":
                        tomorrow_notes.splice(array_index, 1);
                        break;
                    case "3":
                        someday_notes.splice(array_index, 1);
                }

                console.log("success");
            } else {
                console.log("fail"); //TODO пометить красным инпут?
            }
        }
    });

}

function putNoteInTrash(index, list) {
    let up;
    let array_index;
    switch (list.toString()) {
        case "1":
            for (let note of today_notes) {
                if (note.id === index) {
                    array_index = today_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
            break;
        case "2":
            for (let note of tomorrow_notes) {
                if (note.id === index) {
                    array_index = tomorrow_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
            break;
        case "3":
            for (let note of someday_notes) {
                if (note.id === index) {
                    array_index = someday_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
    }

    $.ajax({
        url: "/final_task/TODO/statusInTrash",
        type: "POST",
        data: up,
        dataType: 'JSON',
        success: function (resp) {
            if (resp.status == true) {
                let note;
                $(`#up .note_container_1[id="${index}"]`).hide( "slow" );
                setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).remove(); },600);
                switch (list.toString()) {
                    case "1":
                        note = today_notes[array_index];
                        today_notes.splice(array_index, 1);
                        break;
                    case "2":
                        note = tomorrow_notes[array_index];
                        tomorrow_notes.splice(array_index, 1);
                        break;
                    case "3":
                        note = someday_notes[array_index];
                        someday_notes.splice(array_index, 1);
                }

                deleted_notes.push(note);
                createTrashNoteBlock(note);

                console.log("success");
            } else {
                console.log("fail"); //TODO пометить красным инпут?
            }
        }
    });

}

function deleteNoteFile(index, list) {
    let up;
    let array_index;
    if (list.toString() == "1") {
        for (let note of today_notes) {
            if (note.id === index) {
                array_index = today_notes.indexOf(note);
                up = JSON.stringify({
                    noteUserId: note.user_id,
                    noteId: note.id
                });
                break;
            }
        }
    } else if (list.toString() == "2") {
        for (let note of tomorrow_notes) {
            if (note.id === index) {
                array_index = tomorrow_notes.indexOf(note);
                up = JSON.stringify({
                    noteUserId: note.user_id,
                    noteId: note.id
                });
                break;
            }
        }
    } else if (list.toString() == "3") {
            for (let note of someday_notes) {
                if (note.id === index) {
                    array_index = someday_notes.indexOf(note);
                    up =  JSON.stringify({
                        noteUserId: note.user_id,
                        noteId: note.id
                    });
                    break;
                }
            }
    } else {
        for (let note of deleted_notes) {
            if (note.id === index) {
                array_index = deleted_notes.indexOf(note);
                up =  JSON.stringify({
                    noteUserId: note.user_id,
                    noteId: note.id
                });
                break;
            }
        }
    }

    $.ajax({
        url: "/final_task/TODO/deleteFile",
        type: "POST",
        data: up,
        dataType: 'JSON',
        success: function (resp) {
            console.log(resp);
            if (resp.status == true) {
                $(`.note_container_1[id="${index}"] .note_file_container`).hide( "slow" );
                setTimeout(function() { $(`.note_container_1[id="${index}"] .note_file_container`).remove(); },600);

                if (list.toString() == "1") {
                    if (today_notes[array_index].note == "null") {
                        setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).hide( "slow" ); },600);
                        setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).remove(); },1200);
                        today_notes.splice(array_index, 1);
                    } else {
                        today_notes[array_index].file_path = "null";
                    }

                } else if (list.toString() == "2") {
                    if (tomorrow_notes[array_index].note == "null") {
                        setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).hide( "slow" ); },600);
                        setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).remove(); },1200);
                        tomorrow_notes.splice(array_index, 1);
                    } else {
                        tomorrow_notes[array_index].file_path = "null";
                    }

                }
                else if (list.toString() == "3") {
                    if (someday_notes[array_index].note == "null") {
                        setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).hide( "slow" ); },600);
                        setTimeout(function() { $(`#up .note_container_1[id="${index}"]`).remove(); },1200);
                        someday_notes.splice(array_index, 1);
                    } else {
                        someday_notes[array_index].file_path = "null";
                    }

                }
                else {
                    if (deleted_notes[array_index].note == "null") {
                        setTimeout(function() { $(`#deleted .note_container_1[id="${index}"]`).hide( "slow" ); },600);
                        setTimeout(function() { $(`#deleted .note_container_1[id="${index}"]`).remove(); },1200);
                        deleted_notes.splice(array_index, 1);
                    } else {
                        deleted_notes[array_index].file_path = "null";
                    }

                }

                console.log("success");
            } else {
                console.log("fail"); //TODO пометить красным инпут?
            }
        }
    });

}

function getTrashBin() {
    $('#deleted').find('.note_container_1').remove();

    $('#deleted_container').addClass('animate');
    setTimeout(function() {
        for (let note of deleted_notes) {
            createTrashNoteBlock(note);
        }
    }, 600);
}

function closeTrashBin() {
    $('#deleted_container').removeClass('animate');
    setTimeout(function() { $('#deleted').find('.note_container_1').remove(); },650);
}

function clearTrashBin() {
    if (deleted_notes.length != 0) {
        $.ajax({
            url: "/final_task/TODO/clearTrash",
            type: "POST",
            data: JSON.stringify({ noteUserId: deleted_notes[0].user_id }),
            dataType: 'JSON',
            success: function (resp) {
                console.log(resp);
                if (resp.status == true) {
                    $('#deleted').find('.note_container_1').hide( "slow" );
                    setTimeout(function() { $('#deleted').find('.note_container_1').remove(); },600);
                    deleted_notes.splice(0, deleted_notes.length);
                }
            }
        });
    }

}


function createTrashNoteBlock(note) {
    if (note.note == "null") {
        $(`<div class="note_container_1" id="${note.id}"><div class="note_container_2" id="4">
<div class="note">
<div class="note_file_container">
        <a href="usersFiles/${note.user_id}/${note.file_path}">
            <img id="img_note_file" src="images/File.png" width="25px" height="25px" alt="File">
        </a>
        <div class="note_file"><a href="usersFiles/${note.user_id}/${note.file_path}">${note.file_path.replace(/^[1-9]{4}/g, "")}</a></div>
        <div class="note_file_buttons">
            <button id="delete_file_button" onclick="deleteNoteFile(this.parentNode.parentNode.parentNode.parentNode.parentNode.getAttribute('id'),
                this.parentNode.parentNode.parentNode.parentNode.getAttribute('id'))">
                <img src="images/InTrash.png" width="16px" height="16px" alt="Delete file">
            </button>
        </div>
    </div>
</div>
<div class="note_buttons">
    <button class="recycling_button" onclick="extractNoteFromTrash(this.parentNode.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Recycling.png" width="20px" height="20px" alt="Ok">
    </button>
    <button class="delete_button" onclick="deleteNote(this.parentNode.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Delete.svg" width="20px" height="20px" alt="Delete">
    </button>
</div>

</div>
</div>`).appendTo('#deleted').slideDown();

    } else if (note.file_path == "null") {
        note.note = note.note.replace(/#!/g, "<br/>");

        $(`<div class="note_container_1" id="${note.id}"><div class="note_container_2" id="4">
<div class="note">${note.note}</div>
<div class="note_buttons">
    <button class="recycling_button" onclick="extractNoteFromTrash(this.parentNode.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Recycling.png" width="20px" height="20px" alt="Ok">
    </button>
    <button class="delete_button" onclick="deleteNote(this.parentNode.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Delete.svg" width="20px" height="20px" alt="Delete">
    </button>
</div>

</div>
</div>`).appendTo('#deleted').slideDown();

    } else {
        note.note = note.note.replace(/#!/g, "<br/>");

        $(`<div class="note_container_1" id="${note.id}"><div class="note_container_2" id="4">
<div class="note">${note.note}
    <div class="note_file_container">
        <a href="usersFiles/${note.user_id}/${note.file_path}">
            <img id="img_note_file" src="images/File.png" width="25px" height="25px" alt="File">
        </a>
        <div class="note_file"><a href="usersFiles/${note.user_id}/${note.file_path}">${note.file_path.replace(/^[1-9]{4}/g, "")}</a></div>
        <div class="note_file_buttons">
            <button id="delete_file_button" onclick="deleteNoteFile(this.parentNode.parentNode.parentNode.parentNode.parentNode.getAttribute('id'),
                this.parentNode.parentNode.parentNode.parentNode.getAttribute('id'))">
                <img src="images/InTrash.png" width="16px" height="16px" alt="Delete file">
            </button>
        </div>
    </div>
</div>
<div class="note_buttons">
    <button class="recycling_button" onclick="extractNoteFromTrash(this.parentNode.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Recycling.png" width="20px" height="20px" alt="Ok">
    </button>
    <button class="delete_button" onclick="deleteNote(this.parentNode.parentNode.parentNode.getAttribute('id'))">
        <img src="images/Delete.svg" width="20px" height="20px" alt="Delete">
    </button>
</div>

</div>
</div>`).appendTo('#deleted').slideDown();
    }

}

function extractNoteFromTrash(index) {
    let up;
    let array_index;
    for (let note of deleted_notes) {
        if (note.id === index) {
            array_index = deleted_notes.indexOf(note);
            up =  JSON.stringify({
                noteUserId: note.user_id,
                noteId: note.id
            });
            break;
        }
    }

    $.ajax({
        url: "/final_task/TODO/statusToday",
        type: "POST",
        data: up,
        dataType: 'JSON',
        success: function (resp) {
            if (resp.status == true) {
                $(`#deleted .note_container_1[id="${index}"]`).hide( "slow" );
                setTimeout(function() { $(`#deleted .note_container_1[id="${index}"]`).remove(); },600);
                let note = deleted_notes[array_index];
                today_notes.push(note);
                deleted_notes.splice(array_index, 1);
                if (list == 1) {
                    createNoteBlock(note, today_notes.indexOf(note), 1);
                }

                console.log("success");
            } else {
                console.log("fail"); //TODO пометить красным инпут?
            }
        }
    });

}

function deleteNote(index) {
    let up;
    let array_index;
    for (let note of deleted_notes) {
        if (note.id === index) {
            array_index = deleted_notes.indexOf(note);
            up =  JSON.stringify({
                noteUserId: note.user_id,
                noteId: note.id
            });
            break;
        }
    }

    $.ajax({
        url: "/final_task/TODO/delete",
        type: "POST",
        data: up,
        dataType: 'JSON',
        success: function (resp) {
            if (resp.status == true) {
                $(`#deleted .note_container_1[id="${index}"]`).hide( "slow" );
                setTimeout(function() { $(`#deleted .note_container_1[id="${index}"]`).remove(); },600);
                deleted_notes.splice(array_index, 1);

                console.log("success");
            } else {
                console.log("fail"); //TODO пометить красным инпут?
            }
        }
    });

}

function exit() {
    $.ajax({
        url: "/final_task/TODO/exit",
        type: "POST",
        dataType: 'JSON',
        success: function (resp) {
            if (resp.status == true) {
                console.log("success");
                document.location.href = "../final_task/login.html";
            } else {
                console.log("fail");
            }
        }
    });
}
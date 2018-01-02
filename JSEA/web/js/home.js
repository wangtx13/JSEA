
function enableList(para) {
    if (para.checked) {
        document.getElementById("list").disabled = false;
    } else {
        document.getElementById("list").disabled = true;
    }
}

function checkFiles() {
    var file = document.getElementById("upload_form").files.value;
    if (file == null || file == "") {
        alert("Please select at least one file");
        return false;
    }
    return true;
}

function checkProjectName() {
    var projectName = document.getElementById("projectName").value;
    if (projectName.length == 0)
    {
        alert("Please input the project name.");
        return false;
    }
    return true;
}

function checkTopicCount() {
    var projectName = document.getElementById("topicCount").value;
    if (projectName.length == 0) {
        alert("Please input the number of topic.");
        return false;
    } else if (projectName <= 0) {
        alert("The number of topics is negative. Please input a positive integer.");
        return false;
    } else if (!isInt(projectName)) {
        alert("The number of topics is not an integer. Please input a positive integer.");
        return false;
    }
    return true;
}

function ifSubmit() {
    if (!checkFiles()) return;
    if (!checkProjectName()) return;
    if (!checkTopicCount()) return;
    document.getElementById("upload_form").submit();
}

function isInt(value) {
    return !isNaN(value) &&
        parseInt(Number(value)) == value &&
        !isNaN(parseInt(value, 10));
}

function enableCustomizePackage(para) {
    if (para.checked) {
        document.getElementById("customizePackage").disabled = false;
    } else {
        document.getElementById("customizePackage").disabled = true;
    }
}

function enableTextArea(para) {
    if (para.checked) {
        document.getElementById("textarea").disabled = false;
    } else {
        document.getElementById("textarea").disabled = true;
    }
}
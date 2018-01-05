function checkSearchContent() {
    var hasTopics = document.getElementById("hasTopics");
    var hasDocs = document.getElementsById("hasDocs");
    if (hasTopics.checked) {
        alert("checked");
        return false;
    }
    if (!hasTopics.checked && !hasDocs.checked) {
        alert("Please select search content: \"Topics\" or \"Top 3 Documents\"");
        return false;
    }
    return true;
}

function ifStartSearch() {
    if (!checkSearchContent()) return;
    document.getElementById("search").submit();
}

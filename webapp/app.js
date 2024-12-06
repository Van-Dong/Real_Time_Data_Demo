// const eventSource = new EventSource('http://localhost:8080/sse-server/sse-with-my-data')
// const eventSource = new EventSource('http://localhost:8080/sse-server/sse-with-fetch-data')
const eventSource = new EventSource('http://localhost:8080/sse-server/group-sse')

eventSource.onmessage = function (e) {
    // const obj = JSON.parse(e.data);
    // showData(obj.value);
    const date = e.data
    showData(date)
}

function showData(joke) {
    let table = document.getElementById("jokes");
    let row = table.insertRow(-1);
    let cell = row.insertCell(0);
    cell.innerHTML = joke;
}

window.addEventListener('beforeunload', function() {
    eventSource.close();  // Đảm bảo đóng SSE khi tab bị đóng
 });
  
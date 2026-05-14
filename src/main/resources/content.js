document.querySelectorAll("time").forEach(el => {
    const date = new Date(el.getAttribute("datetime"))
    if(isNaN(date.getTime())) {
        console.warn("Invalid date in element:", el)
        return
    }

    el.innerText = date.toLocaleString()
})
let timeout = null;

function autocompleteAddress() {
    clearTimeout(timeout);
    timeout = setTimeout(async () => {
        const input = document.getElementById('indirizzo').value;
        input.replaceAll(' ', '%20');

        const response = await fetch(`/api/places/osm-autocomplete?query=${encodeURIComponent(input)}`);
        const suggestions = await response.json();

        const list = document.getElementById('suggestions');
        list.innerHTML = '';
        suggestions.forEach(item => {
            const li = document.createElement('li');
            li.className = 'list-group-item list-group-item-action';
            li.textContent = item.displayName;
            li.onclick = () => {
                document.getElementById('indirizzo').value = item.displayName;
                list.innerHTML = '';
                list.style.display = 'none';
                updateMap(item.lat, item.lon);
            };
            list.appendChild(li);
        });
        list.style.display = suggestions.length > 0 ? 'block' : 'none';
    }, 300);
}
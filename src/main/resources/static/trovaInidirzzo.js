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
        if(list.innerHTML === '') {
            list.style.display = 'none';
        } else {
            list.style.display = 'block';
        }
        suggestions.forEach(item => {
            const li = document.createElement('option');
            li.text = item.displayName;
            li.value = item.displayName;
            if(list.innerHTML === '') {
                list.style.display = 'none';
            } else {
                list.style.display = 'block';
            }
            li.onclick = () => {
                document.getElementById('indirizzo').value = item.displayName;
                list.innerHTML = '';
            };
            list.appendChild(li);
        });
    }, 300);
}
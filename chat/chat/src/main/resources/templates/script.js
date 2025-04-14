document.getElementById('ollamaForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // Prevent the form from submitting the traditional way

    const model = document.getElementById('model').value;
    const prompt = document.getElementById('prompt').value;

    const responseContainer = document.getElementById('response');
    responseContainer.textContent = 'Loading...'; // Show loading message

    try {
        const response = await fetch('http://localhost:8080/api/ollama/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ model, prompt }),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.text();
        responseContainer.textContent = data; // Display the response
    } catch (error) {
        responseContainer.textContent = `Error: ${error.message}`; // Display error message
    }
});
// Função de login: Envia username e password
function getToken() {
    var loginUrl = "/authenticate";
    var xhr = new XMLHttpRequest();
    var userElement = document.getElementById('username');
    var passwordElement = document.getElementById('password');
    var messageElement = document.getElementById('message');
    var user = userElement.value;
    var password = passwordElement.value;

    xhr.open('POST', loginUrl, true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    xhr.addEventListener('load', function() {
        var responseObject = JSON.parse(this.response);
        console.log(responseObject);
        if (responseObject.token) {
            localStorage.setItem('token', responseObject.token);
            window.location.replace("/html/imoveis.html");
        } else {
            messageElement.innerHTML = "Username/password inválido!";
        }
    });

    var sendObject = JSON.stringify({ username: user, password: password });

    console.log('going to send', sendObject);

    xhr.send(sendObject);
}

// Função de registro: Envia todos os dados necessários
function registerUser() {
    var registerUrl = "/register";  // Alterar para o endpoint correto de registro
    var xhr = new XMLHttpRequest();
    
    // Coleta os dados do formulário de registro
    var nome = document.getElementById('nomeRegistro').value;
    var sobrenome = document.getElementById('sobrenomeRegistro').value;
    var email = document.getElementById('emailRegistro').value;
    var username = document.getElementById('usernameRegistro').value;
    var password = document.getElementById('passwordRegistro').value;

    // Envia os dados como JSON
    var sendObject = JSON.stringify({
        nome: nome,
        sobrenome: sobrenome,
        email: email,
        username: username,
        password: password
    });

    // Envia os dados para o servidor
    xhr.open('POST', registerUrl, true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    xhr.addEventListener('load', function() {
        var responseObject = JSON.parse(this.response);
        console.log(responseObject);
        if (responseObject.success) {
            alert("Registro realizado com sucesso! Agora você pode fazer login.");
            // Opcionalmente, redireciona para a página de login
            window.location.replace("/html/login.html");
        } else {
            // Exibe uma mensagem de erro
            document.getElementById('mensagemRegistro').innerText = "Erro ao registrar usuário!";
        }
    });

    console.log('going to send', sendObject);
    xhr.send(sendObject);
}

// Função para acessar a API secreta
function getSecret() {
    var url = "/hello";
    var xhr = new XMLHttpRequest();
    var tokenElement = document.getElementById('token');
    var resultElement = document.getElementById('result');
    xhr.open('GET', url, true);
    xhr.setRequestHeader("Authorization", "Bearer " + tokenElement.innerHTML);
    xhr.addEventListener('load', function() {
        var responseObject = JSON.parse(this.response);
        console.log(responseObject);
        resultElement.innerHTML = this.responseText;
    });

    xhr.send(null);
}

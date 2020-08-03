console.log("HELLO")

function passwordValidation() {
    
    var passValidate = document.querySelector('.pd-password-validation');
    // passValidate.setAttribute('pattern', '(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}');
    passValidate.setAttribute('title', 'Password must contain : <br><li>Minimun 8 characters.</li><li>A special character.</li><li>A number.</li>');
    var passMessage = document.querySelector('.pd-password-message');
    if(passMessage){
      passMessage.innerHTML = passValidate.getAttribute('title');
    }
    // passValidate.addEventListener('blur', function(){
    //   passMessage.innerHTML = passValidate.getAttribute('title');
    // });
    passValidate.addEventListener('keyup', function(){
      var passInfo = '';
      passInfo += '<p>Password must contain :</p>';
      // passInfo += '<p id="pd-password-letter" class="pd-password-invalid">Only one or more <b>lowercase</b> letter</p>' 
      // passInfo += '<p id="pd-password-capital" class="pd-password-invalid">Only one or more <b>capital (uppercase)</b> letter</p>' 
      passInfo += '<p id="pd-password-length" class="my-0 pd-password-invalid">Minimum <b>8 characters</b></p>' 
      passInfo += '<p id="pd-password-specialChar" class="my-0 pd-password-invalid">A <b>special character</b></p>'
      passInfo += '<p id="pd-password-number" class="my-0 pd-password-invalid">A <b>number</b></p>'
      passMessage.innerHTML = passInfo;

      var spChar = /[*_$@&~#]/g;
      var splChar = document.getElementById('pd-password-specialChar');
      if(passValidate.value.match(spChar)) {  
        splChar.classList.remove("pd-password-invalid");
        splChar.classList.add("pd-password-valid");
      } else {
        splChar.classList.remove("pd-password-valid");
        splChar.classList.add("pd-password-invalid");
      }

      var numbers = /[0-9]/g;
      var number = document.getElementById('pd-password-number');
      if(passValidate.value.match(numbers)) {  
        number.classList.remove("pd-password-invalid");
        number.classList.add("pd-password-valid");
      } else {
        number.classList.remove("pd-password-valid");
        number.classList.add("pd-password-invalid");
      }
      var length = document.getElementById('pd-password-length');
      if(passValidate.value.length >= 8) {
        length.classList.remove("pd-password-invalid");
        length.classList.add("pd-password-valid");
      } else {
        length.classList.remove("pd-password-valid");
        length.classList.add("pd-password-invalid");
      }
   });

}


function login(){
  console.log("login");
  
  var email = document.getElementById("EmailTextField").value;
  var password = document.getElementById("PasswordTextField").value;

  console.log("email is ",email);
  firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
    // Handle Errors here.
    var errorCode = error.code;
    var errorMessage = error.message;
    // ...
    alert(error.message);

  }).then(function() {
    window.location.replace("Home.html");
  });

}

function signUp(){
  var email = document.getElementById("EmailTextField").value;
  var password = document.getElementById("PasswordTextField").value;
  var name = document.getElementById("NameTextField").value;

  firebase.auth().createUserWithEmailAndPassword(email, password).catch(function(error) {
    
    alert(error.message);
    
   }).then(function(){
     console.log("CREATING USER ");
     var user = firebase.auth().currentUser;
     writeUserData(user.uid, name, email)

   })
}

function writeUserData(userId, name, email) {
  firebase.database().ref('Managers/' + userId).set({
    username: name,
    email: email
    }).then(function() {
    window.location.replace("Home.html");
  }).catch(function(err) {
    alert(error.message);

  });
}
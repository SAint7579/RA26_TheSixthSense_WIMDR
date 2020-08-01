import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'signup.dart';
import 'maps.dart';
import '../services/auth.dart';
import '../models/user.dart';

void main() => runApp(SignIn());

class SignIn extends StatefulWidget {
  const SignIn({this.onSignedIn});
  final VoidCallback onSignedIn;

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<SignIn>{

  final AuthService _auth = AuthService();
  final _formKey = GlobalKey<FormState>();

  String email = '';
  String password = '';
  String error = '';
  String user_id = '';

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
        backgroundColor: Colors.white,
        body: ListView(
          children: <Widget>[
            Container(
              child: Padding(
                padding: EdgeInsets.only(left: 130.0,bottom: 150.0),
                child: Center(
                    child: Text('Ek kadam \n'
                        'swachhata ki '
                        'ore',style: TextStyle(
                    color: Colors.black,
                    fontSize: 21,
                    fontWeight: FontWeight.bold,
                  ),
                    ),
                  ),
              ),
              height: 250,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: AssetImage(
                    'assets/img/wasteman.png',
                  ),fit: BoxFit.fitWidth,
                ),
              ),
            ),
            SizedBox(
              height: 20,
            ),
             Center(
                child: Text('Login',style: TextStyle(
                  color: Colors.black,
                  fontSize: 21,
                  fontWeight: FontWeight.bold,
                ),
                ),
              ),
            SizedBox(
              height: 20,
            ),
            Padding(
              padding: const EdgeInsets.all(20.0),
              child: Form(
                key: _formKey,
                child: Column(
                  children: <Widget>[
//                IconButton(icon: Icon(Icons.person),onPressed: (){},),
                 TextFormField(
                   decoration: InputDecoration(hintText: 'Email'),
                   validator: (val) => val.isEmpty ? 'Enter an email' : null,
                   onChanged: (val){
                     setState(() {
                       email = val;
                     });
                   },
                 ),
                    SizedBox(height: 20.0,),
                    TextFormField(
                      decoration: InputDecoration(hintText: 'Password'),
                      validator: (val) => val.length < 6 ? 'Enter a password 6+ characters long' : null,
                      obscureText: true,
                      onChanged: (val){
                        password = val;
                      },
                    ),
                    SizedBox(height: 20.0,),
                    Center(
                      child: Row(
                        children: <Widget>[
                          SizedBox(width: 30.0,),
                          RaisedButton(
                          color: Colors.green,
                          child: Text(
                            'SIGN IN',
                            style: TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),),
                          onPressed: () async {
                            if (_formKey.currentState.validate()) {
                              dynamic result = await _auth.signInWithEmailAndPassword(email.trim(), password);
                              User user = result;
                              if (result == null) {
                                setState(() {
                                  error = 'Could not sign in';
                                });
                              }
                              else {
                                user_id = user.uid;
                                print(user_id);
                                Navigator.push(
                                    context, MaterialPageRoute(
                                    builder: (context) => MyApp()));
                              }
                            }
                          },
                        ),
                          SizedBox(width: 20.0,),
                          RaisedButton(
                            color: Colors.green,
                            child: Text(
                              'SIGN OUT',
                              style: TextStyle(
                                color: Colors.white,
                                fontWeight: FontWeight.bold,
                              ),),
                            onPressed: () async {
                              await _auth.signOut();
                            },
                          ),
              ],
                      ),
                    ),
                    SizedBox(height: 12.0,),
                    Text(
                      error,
                      style: TextStyle(
                        color: Colors.red,
                        fontSize: 14.0,
                      ),
                    ),
                ],),
              ),
            ),
//            Padding(
//              padding: const EdgeInsets.all(20.0),
//              child: Row(children: <Widget>[
//                IconButton(icon: Icon(Icons.lock),onPressed: (){},),
//                Expanded(
//                  child: Container(
//                    margin: EdgeInsets.only(left: 4,right: 20),
//                    child: TextField(
//                      decoration: InputDecoration(
//                        hintText: 'Password',
//                      ),
//                    ),
//                  ),
//                ),
//              ],),
//            ),
//            SizedBox(
//              height: 20,
//            ),
//           Padding(
//                padding: const EdgeInsets.all(20.0),
//                child:
//              ),
            SizedBox(
              height: 20,
            ),
            InkWell(
              onTap: (){
                Navigator.push(
                    context, MaterialPageRoute(builder: (context) => SignUp()));
              },
              child: Center(
                child: RichText(
                  text: TextSpan(
                    text: 'Don\'t have an account',
                    style: TextStyle(
                      color: Colors.black,
                    ),
                    children: [
                      TextSpan(
                        text: '  SIGN UP',
                        style: TextStyle(
                          color: Colors.green,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      ],
                  ),
                ),
              ),
            ),
          ],
        ),
    );
  }

}